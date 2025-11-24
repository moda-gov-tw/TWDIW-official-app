package gov.moda.dw.verifier.oidvp.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import gov.moda.dw.verifier.oidvp.util.JsonUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "verify_result")
@JsonInclude(Include.NON_NULL)
public class VerifyResultJpa implements Persistable<String> {

    @NotNull
    @Id
    @Column(name = "transaction_id")
    private String transactionId;

    @NotNull
    @Column(name = "response_code")
    private String responseCode;

    /**
     * session create time
     */
    @NotNull
    @Column(name = "request_time")
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestTime;

    @CreationTimestamp
    @Column(name = "response_time")
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime responseTime;

    @Column(name = "vc_claim")
    @ColumnTransformer(write = "?::json")
    private String vcClaim; // json

    @NotNull
    @Column(name = "verify_result")
    private Boolean verifyResult;

    @NotNull
    @Column(name = "result_description")
    private String resultDescription;

    @NotNull
    @Column(name = "error_code")
    private Integer errorCode;

    @Column(name = "holder_did")
    private String holderDid;

    @Column(name = "custom_data")
    private String customData;

    @Transient
    @JsonIgnore
    private List<VcResponseObjectDTO> _vcClaim = null;


    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(LocalDateTime responseTime) {
        this.responseTime = responseTime;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public Boolean getVerifyResult() {
        return verifyResult;
    }

    public void setVerifyResult(Boolean verifyResult) {
        this.verifyResult = verifyResult;
    }

    public String getVcClaim() {
        return vcClaim;
    }

    public List<VcResponseObjectDTO> getVcClaimDTO() {
        return (_vcClaim != null) ? _vcClaim : deserializeVcClaim(vcClaim);
    }

    public void setVcClaim(List<VcResponseObjectDTO> vcResponseObjectDTOs) {
        vcClaim = serializeVcClaim(vcResponseObjectDTOs);
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getHolderDid() {
        return holderDid;
    }

    public void setHolderDid(String holderDid) {
        this.holderDid = holderDid;
    }

    public String getCustomData() {
        return customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    @Override
    @JsonIgnore
    public String getId() {
        return transactionId;
    }

    @Override
    @JsonIgnore
    public boolean isNew() {
        return true;
    }

    private String serializeVcClaim(List<VcResponseObjectDTO> vcResponseObjectDTOs) {
        if (vcResponseObjectDTOs == null) {
            return null;
        }
        return JsonUtils.toJsonString(vcResponseObjectDTOs);
    }

    private List<VcResponseObjectDTO> deserializeVcClaim(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        _vcClaim = JsonUtils.convertJsonArrayStringToObjectList(dbData, VcResponseObjectDTO.class);
        return _vcClaim;
    }
}
