package gov.moda.dw.issuer.oidvci.domain;

import jakarta.persistence.*;
import org.w3c.dom.Text;

import java.sql.Timestamp;

@Entity
@Table(name = "credential_policy")
public class CredentialPolicyEntity {

    @Id
    @Column(name = "pid")
    private String pid;

    @Column(name = "credential_type")
    private String credentialType;

    @Column(name = "effective_duration_time_unit")
    private String effectiveDurationTimeUnit;

    @Column(name = "effective_duration_time_value")
    private Integer effectiveDurationTimeValue;

    @Column(name = "signature_alg")
    private String signatureAlg;

    @Column(name = "created_time")
    private Timestamp createdTime;

    @Column(name = "seq_name")
    private String seqName;

    @Column(name = "schema_id")
    private String schemaId;

    @Column(name = "vc_schema")
    private String vcSchema;

    @Column(name = "func_switch")
    private String funcSwitch;

    // Getters and Setters
    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCredentialType() {
        return credentialType;
    }

    public void setCredentialType(String credentialType) {
        this.credentialType = credentialType;
    }

    public String getEffectiveDurationTimeUnit() {
        return effectiveDurationTimeUnit;
    }

    public void setEffectiveDurationTimeUnit(String effectiveDurationTimeUnit) {
        this.effectiveDurationTimeUnit = effectiveDurationTimeUnit;
    }

    public Integer getEffectiveDurationTimeValue() {
        return effectiveDurationTimeValue;
    }

    public void setEffectiveDurationTimeValue(Integer effectiveDurationTimeValue) {
        this.effectiveDurationTimeValue = effectiveDurationTimeValue;
    }

    public String getSignatureAlg() {
        return signatureAlg;
    }

    public void setSignatureAlg(String signatureAlg) {
        this.signatureAlg = signatureAlg;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public String getSeqName() {
        return seqName;
    }

    public void setSeqName(String seqName) {
        this.seqName = seqName;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getVcSchema() {
        return vcSchema;
    }

    public void setVcSchema(String vcSchema) {
        this.vcSchema = vcSchema;
    }

    public String getFuncSwitch() {
        return funcSwitch;
    }

    public void setFuncSwitch(String funcSwitch) {
        this.funcSwitch = funcSwitch;
    }
}
