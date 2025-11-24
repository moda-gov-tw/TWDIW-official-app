package gov.moda.dw.verifier.vc.task;

import com.danubetech.verifiablecredentials.jwt.JwtVerifiableCredential;
import gov.moda.dw.verifier.vc.service.ResourceLoadService;
import gov.moda.dw.verifier.vc.task.StatusListCheckTask.StatusListCheckResult;
import gov.moda.dw.verifier.vc.task.StatusListCheckTask.StatusListInfo;
import gov.moda.dw.verifier.vc.util.DidUtils;
import gov.moda.dw.verifier.vc.vo.Definition;
import gov.moda.dw.verifier.vc.vo.FutureTaskResult;
import gov.moda.dw.verifier.vc.vo.VpException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class FutureTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FutureTaskService.class);

    private final Executor vcResourceExecutor;
    private final Executor multiVcInVpExecutor;
    private final ResourceLoadService resourceLoadService;

    public FutureTaskService(
        @Qualifier("VcResourceExecutor") Executor vcResourceExecutor,
        @Qualifier("MultiVcInVpExecutor") Executor multiVcInVpExecutor,
        ResourceLoadService resourceLoadService
    )
    {
        this.multiVcInVpExecutor = multiVcInVpExecutor;
        this.resourceLoadService = resourceLoadService;
        this.vcResourceExecutor = vcResourceExecutor;
    }


    public <T> CompletableFuture<FutureTaskResult<T>> doFutureTask(Supplier<FutureTaskResult<T>> supplier, String errorMessage) {
        return CompletableFuture.supplyAsync(supplier, multiVcInVpExecutor)
                                .exceptionally(ex -> exceptionHandler(errorMessage, ex));
    }

    public CompletableFuture<FutureTaskResult<Integer>> validateDidStatus(String issuer) {
        return getDidStatus(issuer).thenApply(result -> {
            if (result.isSuccess()) {
                Integer didStatus = result.getResultData();
                if (didStatus == Definition.ISSUER_DID_STATUS_EFFECTIVE) {
                    return result;
                } else {
                    String msg = "invalid issuer's DID status (" + didStatus + ")";
                    return new FutureTaskResult<Integer>(false)
                        .setCode(VpException.ERR_CRED_INVALID_ISSUER_DID_STATUS)
                        .setMessage(msg);
                }
            } else {
                return result;
            }
        });
    }

    public CompletableFuture<FutureTaskResult<Integer>> getDidStatus(String issuer) {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.debug("get did status");
            try {
                int didStatus = resourceLoadService.callFrontendToQueryDid(issuer);
                return new FutureTaskResult<Integer>(true).setResultData(didStatus);
            } catch (VpException e) {
                LOGGER.error("call did error, {}", e.getMessage());
                return new FutureTaskResult<Integer>(false)
                    .setResultData(-1)
                    .setCode(e.getCode())
                    .setMessage(e.getMessage());
            }
        }, vcResourceExecutor).exceptionally(ex -> exceptionHandler("fail to get did status", ex));
    }

    public CompletableFuture<FutureTaskResult<String>> validateVcSchema(String schemaUrl, Map<String, Object> claims) {
        return getVcSchema(schemaUrl).thenApply(result -> {
            if (result.isSuccess()) {
                String schema = result.getResultData();
                SchemaCheckTask schemaCheckTask = new SchemaCheckTask();
                try {
                    boolean isValid = schemaCheckTask.validate(claims, schema);
                    if (isValid) {
                        return result;
                    } else {
                        return new FutureTaskResult<String>(false)
                            .setCode(VpException.ERR_CRED_VALIDATE_VC_SCHEMA_ERROR)
                            .setMessage("fail to validate vc schema");
                    }
                } catch (VpException e) {
                    return new FutureTaskResult<String>(false)
                        .setCode(e.getCode())
                        .setMessage(e.getMessage());
                }
            } else {
                return result;
            }
        }).exceptionally(ex -> exceptionHandler("fail to validate VcSchema", ex));
    }

    public CompletableFuture<FutureTaskResult<String>> getVcSchema(String schemaUrl) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                LOGGER.debug("load schema");
                String schema = resourceLoadService.loadSchema(schemaUrl);
                return new FutureTaskResult<String>(true).setResultData(schema);
            } catch (VpException e) {
                LOGGER.error("call schema error, {}", e.getMessage());
                return new FutureTaskResult<String>(false)
                    .setCode(e.getCode())
                    .setMessage(e.getMessage());
            }
        }, vcResourceExecutor).exceptionally(ex -> exceptionHandler("fail to get vc schema", ex));
    }

    public CompletableFuture<FutureTaskResult<Map<String, Object>>> validateVcProof(JwtVerifiableCredential jwtVc, String issuer, String jku, String kid) {
        return getIssuerPublicKey(issuer, jku, kid).thenApply(result -> {
            if (result.isSuccess()) {
                CredentialValidateTask credentialValidateTask = new CredentialValidateTask();
                try {
                    Map<String, Object> issuerPublicKet = result.getResultData();
                    boolean isValid = credentialValidateTask.validateJwtProof(jwtVc, issuerPublicKet);
                    if (isValid) {
                        return result;
                    } else {
                        return new FutureTaskResult<Map<String, Object>>(false)
                            .setCode(VpException.ERR_CRED_VALIDATE_VC_PROOF_ERROR)
                            .setMessage("fail to validate vc proof");
                    }
                } catch (VpException e) {
                    return new FutureTaskResult<Map<String, Object>>(false)
                        .setCode(e.getCode())
                        .setMessage(e.getMessage());
                }
            } else {
                return result;
            }
        }).exceptionally(ex -> exceptionHandler("fail to validate VcProof", ex));
    }

    public CompletableFuture<FutureTaskResult<Map<String, Object>>> getIssuerPublicKey(String issuer, String jku, String kid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                LOGGER.debug("load IssuerPublicKey");
                Map<String, Object> issuerPublicKey = DidUtils.extractIssuerPublicKey(issuer);
                return new FutureTaskResult<Map<String, Object>>(true).setResultData(issuerPublicKey);
            } catch (VpException e) {
                try {
                    Map<String, Object> issuerPublicKey = resourceLoadService.loadIssuerPublicKey(jku, kid);
                    return new FutureTaskResult<Map<String, Object>>(true).setResultData(issuerPublicKey);
                } catch (VpException ex) {
                    return new FutureTaskResult<Map<String, Object>>(false)
                        .setCode(e.getCode())
                        .setMessage(e.getMessage());
                }
            }
        }, vcResourceExecutor).exceptionally(ex -> exceptionHandler("fail to get issuerPublicKey", ex));
    }

    public CompletableFuture<FutureTaskResult<StatusListCheckResult>> validateVcStatus(StatusListInfo statusListInfo) {
        return getStatusList(statusListInfo.statusListCredentialURL()).thenApply(result -> {
            LOGGER.debug("do status list check task");
            if (result.isSuccess()) {
                try {
                    StatusListCheckTask statusListCheckTask = new StatusListCheckTask();
                    String statusList = result.getResultData();
                    Integer statusListIndex = statusListInfo.statusListIndex();
                    StatusListCheckResult statusListCheckResult = statusListCheckTask.validate(statusListIndex, statusList);
                    return new FutureTaskResult<StatusListCheckResult>(true).setResultData(statusListCheckResult);
                } catch (VpException e) {
                    return new FutureTaskResult<StatusListCheckResult>(false)
                        .setCode(e.getCode())
                        .setMessage(e.getMessage());
                }
            } else {
                return new FutureTaskResult<StatusListCheckResult>(false)
                    .setCode(result.getCode())
                    .setMessage(result.getMessage());
            }
        }).exceptionally(ex -> exceptionHandler("fail to validate statusList", ex));
    }

    public CompletableFuture<FutureTaskResult<String>> getStatusList(String statusListUrl) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                LOGGER.debug("load statusList");
                String statusList = resourceLoadService.loadStatusList(statusListUrl);
                return new FutureTaskResult<String>(true).setResultData(statusList);
            } catch (VpException e) {
                LOGGER.error("call statusList error, {}", e.getMessage());
                return new FutureTaskResult<String>(false)
                    .setCode(e.getCode())
                    .setMessage(e.getMessage());
            }
        }, vcResourceExecutor).exceptionally(ex -> exceptionHandler("fail to get statusList", ex));
    }

    private <T> FutureTaskResult<T> exceptionHandler(String message, Throwable ex) {
        LOGGER.error("exceptionHandler: {}", message, ex);
        return new FutureTaskResult<T>(false)
            .setCode(VpException.ERR_PRES_VALIDATE_VP_ERROR)
            .setMessage(message + ":" + ex.getMessage());
    }
}
