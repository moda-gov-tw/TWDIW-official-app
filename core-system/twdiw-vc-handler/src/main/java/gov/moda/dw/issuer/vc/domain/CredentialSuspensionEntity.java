package gov.moda.dw.issuer.vc.domain;

import java.sql.Timestamp;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * entity of credential_suspension
 *
 * @version 20250716
 */
@Entity
@Table(name = "credential_suspension")
public class CredentialSuspensionEntity {

	@Id
    @Column(name = "cid")
    private String cid;

    @Column(name = "credential_type")
    private String credentialType;
    
    @Column(name = "suspension_begin_time")
    private Timestamp suspensionBeginTime;
    
    @Column(name = "suspension_duration")
    private int suspensionDuration;
    
    @Column(name = "suspension_duration_unit")
    private String suspensionDurationUnit;
    
    @Column(name = "expected_recovery_time")
    private Timestamp expectedRecoveryTime;
    
    public CredentialSuspensionEntity() {
    	
    }

	public String getCid() {
		return cid;
	}

	public String getCredentialType() {
		return credentialType;
	}

	public Timestamp getSuspensionBeginTime() {
		return suspensionBeginTime;
	}

	public int getSuspensionDuration() {
		return suspensionDuration;
	}

	public String getSuspensionDurationUnit() {
		return suspensionDurationUnit;
	}

	public Timestamp getExpectedRecoveryTime() {
		return expectedRecoveryTime;
	}

	public CredentialSuspensionEntity setCid(String cid) {
		this.cid = cid;
		return this;
	}

	public CredentialSuspensionEntity setCredentialType(String credentialType) {
		this.credentialType = credentialType;
		return this;
	}

	public CredentialSuspensionEntity setSuspensionBeginTime(Timestamp suspensionBeginTime) {
		this.suspensionBeginTime = suspensionBeginTime;
		return this;
	}

	public CredentialSuspensionEntity setSuspensionDuration(int suspensionDuration) {
		this.suspensionDuration = suspensionDuration;
		return this;
	}

	public CredentialSuspensionEntity setSuspensionDurationUnit(String suspensionDurationUnit) {
		this.suspensionDurationUnit = suspensionDurationUnit;
		return this;
	}

	public CredentialSuspensionEntity setExpectedRecoveryTime(Timestamp expectedRecoveryTime) {
		this.expectedRecoveryTime = expectedRecoveryTime;
		return this;
	}
    
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
