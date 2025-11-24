package gov.moda.dw.issuer.vc.vo;

import gov.moda.dw.issuer.vc.util.DateUtils.TimeUnit;

/**
 * credential policy
 *
 * @version 20240902
 */
public class Policy {

    private String pid;
    private String credentialType;
    private String schemaName;
    private String schemaContent;
    private Duration effectiveDuration;
    private String signatureAlgorithm;
    
    private String seqName;
    private String schemaId;

    public String getPid() {
        return pid;
    }

    public Policy setPid(String pid) {
        this.pid = pid;
        return this;
    }

    public String getCredentialType() {
        return credentialType;
    }

    public Policy setCredentialType(String credentialType) {
        this.credentialType = credentialType;
        return this;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public Policy setSchemaName(String schemaName) {
        this.schemaName = schemaName;
        return this;
    }

    public String getSchemaContent() {
        return schemaContent;
    }

    public Policy setSchemaContent(String schemaContent) {
        this.schemaContent = schemaContent;
        return this;
    }

    public Duration getEffectiveDuration() {
        return effectiveDuration;
    }

    public Policy setEffectiveDuration(Duration effectiveDuration) {
        this.effectiveDuration = effectiveDuration;
        return this;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public Policy setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
        return this;
    }

    public String getSeqName() {
		return seqName;
	}

	public Policy setSeqName(String seqName) {
		this.seqName = seqName;
		return this;
	}

	public String getSchemaId() {
		return schemaId;
	}

	public Policy setSchemaId(String schemaId) {
		this.schemaId = schemaId;
		return this;
	}

	public static class Duration {

        private int value;
        private TimeUnit unit;

        public Duration(int value, TimeUnit unit) {
            this.value = value;
            this.unit = unit;
        }

        public int getValue() {
            return value;
        }

        public Duration setValue(int value) {
            this.value = value;
            return this;
        }

        public TimeUnit getUnit() {
            return unit;
        }

        public Duration setUnit(TimeUnit unit) {
            this.unit = unit;
            return this;
        }
    }
}
