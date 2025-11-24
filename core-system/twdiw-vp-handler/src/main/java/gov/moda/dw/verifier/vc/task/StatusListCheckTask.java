package gov.moda.dw.verifier.vc.task;

import com.danubetech.verifiablecredentials.jwt.JwtVerifiableCredential;
import gov.moda.dw.verifier.vc.service.ResourceLoadService;
import gov.moda.dw.verifier.vc.util.ZipUtils;
import gov.moda.dw.verifier.vc.vo.VpException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatusListCheckTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusListCheckTask.class);

    private static ResourceLoadService resourceLoadService;

    @Autowired
    public void setResourceLoadService(ResourceLoadService resourceLoadService) {
        StatusListCheckTask.resourceLoadService = resourceLoadService;
    }

    public StatusListCheckResult validate(int statusListIndex, String statusList) throws VpException {

        boolean vcStatusOK;
        String statusPurpose;
        try {
            JwtVerifiableCredential jwt = JwtVerifiableCredential.fromCompactSerialization(statusList);

            StatusListValidateTask statusListValidateTask = new StatusListValidateTask();

            // step 1: validate status list content
            StatusListValidateTask.ContentResult contentResult = statusListValidateTask.validateJwtContent(jwt);
            boolean contentOK = contentResult.isContentOK();
            String encodedList = contentResult.getEncodedList();
            String jku = contentResult.getJku();
            String kid = contentResult.getKid();
            statusPurpose = contentResult.getStatusPurpose();
            if (!contentOK || encodedList == null || encodedList.isBlank() || jku == null || kid == null) {
                throw new VpException(VpException.ERR_SL_VALIDATE_STATUS_LIST_CONTENT_ERROR, "fail to validate status list content");
            }

            // step 2: validate status list proof
            // change to use statusList's jku to validate
            Map<String, Object> issuerPublicKey = resourceLoadService.loadIssuerPublicKey(jku, kid);
            boolean vcProofOK = statusListValidateTask.validateJwtProof(jwt, issuerPublicKey);
            if (!vcProofOK) {
                throw new VpException(VpException.ERR_SL_VALIDATE_STATUS_LIST_PROOF_ERROR, "fail to validate status list proof");
            }

            // step 3: validate vc status
            vcStatusOK = checkFromEncodedList(statusListIndex, encodedList);
            if (vcStatusOK) {
                LOGGER.info("[check from status list]: PASS");
            } else {
                LOGGER.error("[check from status list]: FAIL (statusPurpose={})", statusPurpose);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VpException(VpException.ERR_CRED_VALIDATE_VC_STATUS_ERROR, "fail to check from status list");
        }

        return new StatusListCheckResult(vcStatusOK, statusPurpose);
    }

    public static boolean resolveStatusListCheckResult(List<StatusListCheckResult> checkResultList) {
        if (checkResultList.isEmpty()) {
            throw new IllegalArgumentException("checkResultList must not be empty");
        }
        if (checkResultList.size() == 1) {
            return checkResultList.get(0).vcStatusOK();
        }

        boolean result = true;
        String statusPurpose;
        for (StatusListCheckResult statusListCheckResult : checkResultList) {
            statusPurpose = statusListCheckResult.statusPurpose();
            boolean vcStatusOK = statusListCheckResult.vcStatusOK();
            result &= vcStatusOK;
        }
        return result;
    }

    private boolean checkFromEncodedList(int index, String encodedList) {

        // uncompress
        byte[] bytes = ZipUtils.gzipUncompress(encodedList);

        // search target index
        int byteIndex = index / 8;
        int bitIndex = index % 8;

        byte[] checkMask = new byte[]{
            (byte) 0x80, (byte) 0x40, (byte) 0x20, (byte) 0x10,
            (byte) 0x08, (byte) 0x04, (byte) 0x02, (byte) 0x01
        };

        if ((bytes[byteIndex] & checkMask[bitIndex]) == checkMask[bitIndex]) {
            // REVOKED
            return false;
        } else {
            // ACTIVE
            return true;
        }
    }

    public record StatusListCheckResult(boolean vcStatusOK, String statusPurpose) {

        public StatusListCheckResult(boolean vcStatusOK, String statusPurpose) {
            this.vcStatusOK = vcStatusOK;
            this.statusPurpose = statusPurpose == null ? "" : statusPurpose;
        }
    }

    public record StatusListInfo(String statusListCredentialURL, Integer statusListIndex, String statusPurpose) {

        public StatusListInfo(String statusListCredentialURL, Integer statusListIndex, String statusPurpose) {
            if (statusListCredentialURL == null || statusListCredentialURL.isEmpty()) {
                throw new IllegalArgumentException("statusListCredential must not be empty.");
            }
            this.statusListCredentialURL = statusListCredentialURL;

            if (statusListIndex == null || statusListIndex < 0) {
                throw new IllegalArgumentException("statusListIndex must be positive.");
            }
            this.statusListIndex = statusListIndex;

            this.statusPurpose = statusPurpose == null ? "" : statusPurpose;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"statusListCredentialURL\":\"")
              .append(statusListCredentialURL).append('\"');
            sb.append(",\"statusListIndex\":")
              .append(statusListIndex);
            sb.append(",\"statusPurpose\":\"")
              .append(statusPurpose).append('\"');
            sb.append("}");
            return sb.toString();
        }
    }
}
