package gov.moda.dw.issuer.vc.domain;

import java.sql.Timestamp;

import gov.moda.dw.issuer.vc.util.DateUtils.TimeUnit;
import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * entity of credential_policy
 *
 * @version 20241016
 */
@Entity //標示為實體類別
@Table(name = "credential_policy") //DB_TABLE_NAME
public class CredentialPolicyEntity {

	@Id //標示何者為Primary Key
	@Column(name="pid")
	private String pid;
	
	@Column(name="credential_type")
	private String credentialType;
	
	@Enumerated(EnumType.STRING)
	@Column(name="effective_duration_time_unit")
	private TimeUnit effectiveDurationTimeUnit;
	
	@Column(name="effective_duration_time_value")
	private int effectiveDurationTimeValue;
	
	@Column(name="signature_alg")
	private String signatureAlg;
	
	@Column(name="created_time")
	private Timestamp createdTime;
	
	@Column(name="seq_name")
	private String seqName;
	
	@Column(name="schema_id")
	private String schemaId;
	
	@Column(name="vc_schema")
	private String vcSchema;
	
	@Column(name="func_switch")
	private String funcSwitch;
	
	public String getPid() {
		return pid;
	}

	public CredentialPolicyEntity setPid(String pid) {
		this.pid = pid;
		return this;
	}

	public String getCredentialType() {
		return credentialType;
	}

	public CredentialPolicyEntity setCredentialType(String credentialType) {
		this.credentialType = credentialType;
		return this;
	}

	public TimeUnit getEffectiveDurationTimeUnit() {
		return effectiveDurationTimeUnit;
	}

	public CredentialPolicyEntity setEffectiveDurationTimeUnit(TimeUnit effectiveDurationTimeUnit) {
		this.effectiveDurationTimeUnit = effectiveDurationTimeUnit;
		return this;
	}

	public int getEffectiveDurationTimeValue() {
		return effectiveDurationTimeValue;
	}

	public CredentialPolicyEntity setEffectiveDurationTimeValue(int effectiveDurationTimeValue) {
		this.effectiveDurationTimeValue = effectiveDurationTimeValue;
		return this;
	}

	public String getSignatureAlg() {
		return signatureAlg;
	}

	public CredentialPolicyEntity setSignatureAlg(String signatureAlg) {
		this.signatureAlg = signatureAlg;
		return this;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public CredentialPolicyEntity setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
		return this;
	}

	public String getSeqName() {
		return seqName;
	}

	public CredentialPolicyEntity setSeqName(String seqName) {
		this.seqName = seqName;
		return this;
	}

	public String getSchemaId() {
		return schemaId;
	}

	public CredentialPolicyEntity setSchemaId(String schemaId) {
		this.schemaId = schemaId;
		return this;
	}

	public String getVcSchema() {
		return vcSchema;
	}

	public CredentialPolicyEntity setVcSchema(String vcSchema) {
		this.vcSchema = vcSchema;
		return this;
	}

	public String getFuncSwitch() {
		return funcSwitch;
	}

	public CredentialPolicyEntity setFuncSwitch(String funcSwitch) {
		this.funcSwitch = funcSwitch;
		return this;
	}

	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
