package gov.moda.dw.verifier.oidvp.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import gov.moda.dw.verifier.oidvp.model.oid4vp.OidvpResponseMode;
import gov.moda.dw.verifier.oidvp.model.oid4vp.OidvpResponseType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "session")
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionJpa implements Persistable<String> {

    @Transient public static final boolean NOT_VALIDATED = false;
    @Transient public static final boolean VALIDATED = true;

    @NotNull
    @Id
    @Column(name = "transaction_id")
    private String transactionId;

    @NotNull
    @Column(name = "client_id")
    private String clientId;

    @NotNull
    @Column(name = "nonce")
    private String nonce;

    @NotNull
    @Column(name = "state")
    private String state;

    @NotNull
    @Column(name = "rm")
    private Integer responseMode;

    @NotNull
    @Column(name = "rt")
    private Integer responseType;

    @CreationTimestamp
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @NotNull
    @Column(name = "expired_time")
    private LocalDateTime expiredTime;

    @NotNull
    @Column(name = "ref")
    private String ref;

    @ColumnTransformer(write = "?::json")
    @Column(name = "presentation_definition")
    private String presentationDefinition; // json

    @Column(name = "validated")
    private boolean validated = NOT_VALIDATED;

    @Column(name = "callback")
    private String callback;

    @Transient
    private boolean isNew = true;


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public LocalDateTime getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(LocalDateTime expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getPresentationDefinition() {
        return presentationDefinition;
    }

    public void setPresentationDefinition(String presentationDefinition) {
        this.presentationDefinition = presentationDefinition;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public boolean getValidated() {
        return validated;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public void setUpdateMode() {
        isNew = false;
    }

    public ResponseMode getResponseMode() {
        return OidvpResponseMode.getResponseModeByIndex(responseMode);
    }

    public void setResponseMode(ResponseMode responseMode) {
        this.responseMode = OidvpResponseMode.getResponseModeIndex(responseMode);
    }

    public ResponseType getResponseType() {
        return OidvpResponseType.getResponseTypeByIndex(responseType);
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = OidvpResponseType.getResponseTypeIndex(responseType);
    }

    @Override
    public String getId() {
        return transactionId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
