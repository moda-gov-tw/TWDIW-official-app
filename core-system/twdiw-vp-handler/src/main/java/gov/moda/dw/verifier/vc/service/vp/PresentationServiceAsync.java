package gov.moda.dw.verifier.vc.service.vp;

import com.authlete.sd.Disclosure;
import com.authlete.sd.SDJWT;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.jwt.JwtVerifiableCredential;
import com.danubetech.verifiablecredentials.jwt.JwtVerifiablePresentation;
import com.nimbusds.jwt.JWTClaimsSet;
import gov.moda.dw.verifier.vc.service.dto.ErrorResponseProperty;
import gov.moda.dw.verifier.vc.service.dto.ErrorResponseDTO;
import gov.moda.dw.verifier.vc.service.dto.PresentationValidationResponseDTO;
import gov.moda.dw.verifier.vc.service.dto.PresentationValidationResponseDTO.VcData;
import gov.moda.dw.verifier.vc.task.CredentialValidateTask;
import gov.moda.dw.verifier.vc.task.FutureTaskService;
import gov.moda.dw.verifier.vc.task.PresentationValidateTask;
import gov.moda.dw.verifier.vc.task.StatusListCheckTask;
import gov.moda.dw.verifier.vc.task.StatusListCheckTask.StatusListCheckResult;
import gov.moda.dw.verifier.vc.task.StatusListCheckTask.StatusListInfo;
import gov.moda.dw.verifier.vc.util.JsonUtils;
import gov.moda.dw.verifier.vc.util.LogUtils;
import gov.moda.dw.verifier.vc.util.Tuple;
import gov.moda.dw.verifier.vc.vo.FutureTaskResult;
import gov.moda.dw.verifier.vc.vo.VpException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PresentationServiceAsync {

    private static final Logger LOGGER = LoggerFactory.getLogger(PresentationServiceAsync.class);

    private final FutureTaskService futureTaskService;


    public PresentationServiceAsync(FutureTaskService futureTaskService) {
        this.futureTaskService = futureTaskService;
    }

    /**
     * [dwverifier-103i] validate presentation
     *
     * @param presentationList presentation list
     * @return (validation response, http status)
     */
    public Tuple.Pair<String, HttpStatus> validate(List<String> presentationList) {
        String result;
        HttpStatus httpStatus;

        try {
            if (presentationList == null || presentationList.isEmpty()) {
                throw new VpException(VpException.ERR_PRES_INVALID_PRESENTATION_VALIDATION_REQUEST, "invalid presentation validation request");
            }
            LOGGER.info("vp number = {}", presentationList.size());
            List<PresentationValidationResponseDTO> validationResponseList = validateVPs(presentationList);

            result = JsonUtils.voToJs(validationResponseList);
            httpStatus = HttpStatus.OK;
        } catch (VpException e) {
            LOGGER.error(e.getMessage(), e);
            result = new ErrorResponseDTO((ErrorResponseProperty) e).toString();
            httpStatus = e.toHttpStatus();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            VpException vpException = new VpException(VpException.ERR_PRES_VALIDATE_VP_ERROR, "fail to validate vp");
            result = new ErrorResponseDTO(vpException).toString();
            httpStatus = vpException.toHttpStatus();
        }

        return Tuple.collect(result, httpStatus);
    }

    private List<PresentationValidationResponseDTO> validateVPs(List<String> presentationList)
        throws ParseException, VpException
    {
        List<PresentationValidationResponseDTO> responseList = new ArrayList<>();
        boolean isArray = presentationList.size() > 1;

        for (int vpIndex = 0; vpIndex < presentationList.size(); vpIndex++) {
            String presentation = presentationList.get(vpIndex);
            if (presentation == null || presentation.isBlank()) {
                continue;
            }
            VpIndexInfo vpIndexInfo = new VpIndexInfo(vpIndex, isArray);
            PresentationValidationResponseDTO presentationValidationResponseDTO = validateVP(presentation, vpIndexInfo);

            responseList.add(presentationValidationResponseDTO);
            LOGGER.info("vp[{}]: vp validate tasks are completed", vpIndex);
        }
        return responseList;
    }

    private PresentationValidationResponseDTO validateVP(String presentation, VpIndexInfo vpIndexInfo)
        throws ParseException, VpException
    {
        // re-construct JWT verifiable presentation
        JwtVerifiablePresentation jwtVp = JwtVerifiablePresentation.fromCompactSerialization(presentation);
        PresentationValidateTask presentationValidateTask = new PresentationValidateTask();

        // step 1: validate vp content
        PresentationValidateTask.ContentResult vpContentResult = presentationValidateTask.validateJwtContent(jwtVp);
        boolean vpContentOK = vpContentResult.isContentOK();
        String clientId = vpContentResult.getClientId();
        String nonce = vpContentResult.getNonce();
        List<String> vcList = vpContentResult.getVcList();

        if (!vpContentOK || vcList == null || vcList.isEmpty()) {
            throw new VpException(VpException.ERR_PRES_VALIDATE_VP_CONTENT_ERROR, "fail to validate vp content");
        }

        // step 2: validate vc
        LOGGER.info("vc number = {}", vcList.size());
        List<PresentationValidationResponseDTO.VcData> dataList = validateVCsAsync(vcList, vpIndexInfo);

        // confirm the consistency of every holder public key
        Set<Map<String, Object>> holderPublicKeySet = new HashSet<>();
        dataList.forEach(k -> holderPublicKeySet.add(k.getHolderPublicKey()));
        if (holderPublicKeySet.size() != 1) {
            throw new VpException(VpException.ERR_PRES_HOLDER_PUBLIC_KEY_INCONSISTENT, "holder public key is inconsistent in vc");
        }
        Map<String, Object> holderPublicKey = holderPublicKeySet.stream().findFirst().orElse(null);

        // step 3: validate vp proof
        boolean vpProofOK = presentationValidateTask.validateJwtProof(jwtVp, holderPublicKey);
        if (!vpProofOK) {
            throw new VpException(VpException.ERR_PRES_VALIDATE_VP_PROOF_ERROR, "fail to validate vp proof");
        }

        return new PresentationValidationResponseDTO()
            .setClientId(clientId)
            .setNonce(nonce)
            .setHolderDid(getSub(dataList))
            .setVcs(dataList);
    }

    private List<VcData> validateVCsAsync(List<String> vcList, VpIndexInfo vpIndexInfo) throws VpException {
        ArrayList<CompletableFuture<FutureTaskResult<VcData>>> futureTasks = new ArrayList<>();
        for (int i = 0; i < vcList.size(); i++) {
            String credential = vcList.get(i);
            int vcIndex = i;
            futureTasks.add(futureTaskService.doFutureTask(() ->
            {
                LogUtils.addVcPath("vp_path=" + getVpPath(vpIndexInfo) + ",vc_index=" + vcIndex);
                FutureTaskResult<VcData> result = doValidateVC(credential, vpIndexInfo, vcIndex);
                LogUtils.clearAll();
                return result;
            }, "do validateVCs error"));
        }
        return getVcDataFromFutureTask(futureTasks);
    }

    private FutureTaskResult<VcData> doValidateVC(String credential, VpIndexInfo vpIndexInfo, int vcIndex) {
        try {
            VcData vcData = validateVC(credential, vpIndexInfo, vcIndex);
            return new FutureTaskResult<VcData>(true).setResultData(vcData);
        } catch (VpException e) {
            return new FutureTaskResult<VcData>(false)
                .setCode(e.getCode())
                .setMessage(getVcLocation(vpIndexInfo, vcIndex) + " -> " + e.getMessage());
        } catch (ParseException e) {
            LOGGER.error("parse jwt vc error", e);
            return new FutureTaskResult<VcData>(false)
                .setCode(VpException.ERR_PRES_VALIDATE_VP_ERROR)
                .setMessage(getVcLocation(vpIndexInfo, vcIndex) + " -> " + e.getMessage());
        }
    }

    public VcData validateVC(String credential, VpIndexInfo vpIndexInfo, int vcIndex)
        throws VpException, ParseException
    {
        // re-construct JWT verifiable credential
        SDJWT sdJwt = SDJWT.parse(credential);
        String credentialJwt = sdJwt.getCredentialJwt();
        JwtVerifiableCredential jwtVc = JwtVerifiableCredential.fromCompactSerialization(credentialJwt);
        List<Disclosure> disclosureList = sdJwt.getDisclosures();

        // step 1: check issuer's DID
        JWTClaimsSet jwtClaimsSet = jwtVc.getPayload();
        String iss = jwtClaimsSet.getIssuer();
        if (iss == null || iss.isBlank()) {
            throw new VpException(VpException.ERR_CRED_INVALID_ISSUER_DID_FORMAT, "issuer's DID is null or empty");
        }

        CredentialValidateTask credentialValidateTask = new CredentialValidateTask();

        // step 2: validate vc content
        CredentialValidateTask.ContentResult vcContentResult = credentialValidateTask.validateJwtContent(jwtVc, disclosureList);
        boolean vcContentOK = vcContentResult.isContentOK();
        VerifiableCredential vc = vcContentResult.getVc();
        if (!vcContentOK || vc == null) {
            throw new VpException(VpException.ERR_CRED_VALIDATE_VC_CONTENT_ERROR, "fail to validate vc content");
        }
        String sub = jwtClaimsSet.getSubject();

        List<CompletableFuture<? extends FutureTaskResult<?>>> futureTasks = new ArrayList<>();
        List<CompletableFuture<FutureTaskResult<StatusListCheckResult>>> statusListCheckTasks = new ArrayList<>();

        // step 3: validate vc status
        for (StatusListInfo statusListInfo : vcContentResult.getStatusListInfo()) {
            CompletableFuture<FutureTaskResult<StatusListCheckResult>> validateVcStatus = futureTaskService.validateVcStatus(statusListInfo);
            statusListCheckTasks.add(validateVcStatus);
        }

        // step 4: validate issuer's DID status
        CompletableFuture<FutureTaskResult<Integer>> validateDidStatus = futureTaskService.validateDidStatus(iss);
        futureTasks.add(validateDidStatus);

        // step 5: validate vc schema
        String schemaUrl = vcContentResult.getSchemaUrl();
        Map<String, Object> claims = vc.getCredentialSubject().getClaims();
        CompletableFuture<FutureTaskResult<String>> validateVcSchema = futureTaskService.validateVcSchema(schemaUrl, claims);
        futureTasks.add(validateVcSchema);

        // step 6: validate vc proof
        CompletableFuture<FutureTaskResult<Map<String, Object>>> validateVcProof = futureTaskService.validateVcProof(jwtVc, iss, vcContentResult.getJku(), vcContentResult.getKid());
        futureTasks.add(validateVcProof);

        // collect holder's public key from every vc
        Map<String, Object> holderPublicKey = vcContentResult.getHolderPublicKey();

        if (!resolveStatusListCheckResult(statusListCheckTasks)) {
            throw new VpException(VpException.ERR_CRED_VALIDATE_VC_STATUS_ERROR, "vc status is invalid");
        }

        if (checkFutureTaskValidateResult(futureTasks)) {
            LOGGER.info("vp[{}] -> vc[{}]: vc validate tasks are completed", vpIndexInfo.vpIndex(), vcIndex);
        }

        return new VcData().setVpPath(getVpPath(vpIndexInfo))
                           .setVcPath(getVcPath(vcIndex))
                           .setHolderPublicKey(holderPublicKey)
                           .setCredential(vc.toMap())
                           .setSub(sub)
                           .setLimitDisclosureSupported(true);
    }

    private List<VcData> getVcDataFromFutureTask(List<CompletableFuture<FutureTaskResult<VcData>>> completableFutures)
        throws VpException
    {
        ArrayList<VcData> futureTaskResults = new ArrayList<>();
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
        for (CompletableFuture<FutureTaskResult<VcData>> completableFuture : completableFutures) {
            FutureTaskResult<? extends VcData> result = completableFuture.join();
            if (result.isSuccess()) {
                futureTaskResults.add(result.getResultData());
            } else {
                throw new VpException(result.getCode(), result.getMessage());
            }
        }
        return futureTaskResults;
    }

    private boolean checkFutureTaskValidateResult(List<CompletableFuture<? extends FutureTaskResult<?>>> completableFutures)
        throws VpException
    {
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
        for (CompletableFuture<? extends FutureTaskResult<?>> completableFuture : completableFutures) {
            FutureTaskResult<?> result = completableFuture.join();
            if (!result.isSuccess()) {
                throw new VpException(result.getCode(), result.getMessage());
            }
        }
        return true;
    }

    private boolean resolveStatusListCheckResult(List<CompletableFuture<FutureTaskResult<StatusListCheckResult>>> results) throws VpException {
        CompletableFuture.allOf(results.toArray(new CompletableFuture[0])).join();
        ArrayList<StatusListCheckResult> statusListCheckResults = new ArrayList<>();
        for (CompletableFuture<FutureTaskResult<StatusListCheckResult>> result : results) {
            FutureTaskResult<StatusListCheckResult> listCheckResult = result.join();
            if (!listCheckResult.isSuccess()) {
                throw new VpException(listCheckResult.getCode(), listCheckResult.getMessage());
            }
            statusListCheckResults.add(listCheckResult.getResultData());
        }
        return StatusListCheckTask.resolveStatusListCheckResult(statusListCheckResults);
    }

    private String getVcLocation(VpIndexInfo vpIndexInfo, int vcIndex) {
        return "("
            + "vp_path=" + (vpIndexInfo.isArray() ? "$[" + vpIndexInfo.vpIndex() + "]" : "$")
            + ","
            + "vc_path="
            + "$.vp.verifiableCredential[" + vcIndex + "]"
            + ")";
    }

    private String getSub(List<VcData> vcData) throws VpException {
        List<String> subList = vcData.stream().map(VcData::getSub).distinct().toList();
        if (subList.isEmpty()) {
            throw new VpException(VpException.ERR_CRED_LACK_OF_SUB, "lack of sub in vcData");
        } else if (subList.size() != 1) {
            throw new VpException(VpException.ERR_PRES_HOLDER_PUBLIC_KEY_INCONSISTENT, "vc sub is inconsistent");
        }
        return subList.get(0);
    }

    public record VpIndexInfo(int vpIndex, boolean isArray) {}

    private String getVpPath(VpIndexInfo vpIndexInfo) {
        return vpIndexInfo.isArray() ? "$[" + vpIndexInfo.vpIndex() + "]" : "$";
    }

    private String getVcPath(int vcIndex) {
        return "$.vp.verifiableCredential[" + vcIndex + "]";
    }
}
