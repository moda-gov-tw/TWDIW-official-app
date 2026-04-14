package gov.moda.dw.manager.service.dto.custom;

public class DwIssuerVC501iReq {

    private String transactionId;
    private String credentialType;

	public OptionsDTO getOptions() {
		return options;
	}

	public void setOptions(OptionsDTO options) {
		this.options = options;
	}

	public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getCredentialType() {
        return credentialType;
    }

    public void setCredentialType(String credentialType) {
        this.credentialType = credentialType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    private Object data;
    
    private OptionsDTO options;
}
