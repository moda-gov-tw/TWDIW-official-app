package gov.moda.dw.verifier.oidvp.model;

import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.domain.VcResponseObjectDTO;
import gov.moda.dw.verifier.oidvp.domain.VerifyResultJpa;
import gov.moda.dw.verifier.oidvp.service.oidvp.customData.CustomData;
import java.util.List;

public class VerifyResult {

    private Boolean verifyResult;
    private String resultDescription;
    private OidvpError errorCode;
    private String holderDid = "";
    private List<VcResponseObjectDTO> vcClaims;
    private CustomData customData;

    public VerifyResult() {
    }

    public VerifyResult(VerifyResultJpa verifyResultJpa) {
        this.verifyResult = verifyResultJpa.getVerifyResult();
        this.resultDescription = verifyResultJpa.getResultDescription();
        this.errorCode = OidvpError.getOidvpError(verifyResultJpa.getErrorCode());
        this.holderDid = verifyResultJpa.getHolderDid() == null ? "" : verifyResultJpa.getHolderDid();
        this.vcClaims = verifyResultJpa.getVcClaimDTO();
        this.customData = verifyResultJpa.getCustomData() != null ? new CustomData(verifyResultJpa.getCustomData()) : null;
    }

    public static VerifyResult success(String holderDid, List<VcResponseObjectDTO> vcClaims) {
        return new VerifyResult()
            .setVerifyResult(true)
            .setResultDescription("success")
            .setErrorCode(OidvpError.SUCCESS)
            .setHolderDid(holderDid)
            .setVcClaims(vcClaims);
    }

    public static VerifyResult fail(OidvpError error, String resultDescription) {
        return new VerifyResult()
            .setVerifyResult(false)
            .setResultDescription(resultDescription)
            .setErrorCode(error);
    }

    public static VerifyResult fail(String holderDid, OidvpError error, String resultDescription) {
        VerifyResult result = new VerifyResult();
        result.setVerifyResult(false);
        result.setHolderDid(holderDid);
        result.setResultDescription(resultDescription);
        result.setErrorCode(error);
        return result;
    }

    public List<VcResponseObjectDTO> getVcClaims() {
        return vcClaims;
    }

    public VerifyResult setVcClaims(List<VcResponseObjectDTO> vcClaims) {
        this.vcClaims = vcClaims;
        return this;
    }

    public OidvpError getErrorCode() {
        return errorCode;
    }

    public VerifyResult setErrorCode(OidvpError errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getHolderDid() {
        return holderDid;
    }

    public VerifyResult setHolderDid(String holderDid) {
        this.holderDid = holderDid;
        return this;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public VerifyResult setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
        return this;
    }

    public Boolean getVerifyResult() {
        return verifyResult;
    }

    public VerifyResult setVerifyResult(Boolean verifyResult) {
        this.verifyResult = verifyResult;
        return this;
    }

    public CustomData getCustomData() {
        return customData;
    }

    public VerifyResult setCustomData(CustomData customData) {
        this.customData = customData;
        return this;
    }
}
