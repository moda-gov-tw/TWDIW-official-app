package gov.moda.dw.manager.service.dto;

public class VC503iResp {

    private String credentialType;

    public String getCredentialType() {
        return credentialType;
    }

    public void setCredentialType(String credentialType) {
        this.credentialType = credentialType;
    }

    @Override
    public String toString() {
        return "VC503iResp{" + "credentialType='" + credentialType + '\'' + '}';
    }
}
