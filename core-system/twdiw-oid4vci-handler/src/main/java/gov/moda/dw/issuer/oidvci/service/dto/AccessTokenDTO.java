package gov.moda.dw.issuer.oidvci.service.dto;

import gov.moda.dw.issuer.oidvci.domain.AccessToken;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link AccessToken} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AccessTokenDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 140)
    private String accessToken;

    @NotNull
    @Size(max = 140)
    private String accessTokenName;

    @Size(max = 140)
    private String owner;

    @Size(max = 140)
    private String ownerName;

    @NotNull
    @Size(max = 140)
    private String orgId;

    @Size(max = 140)
    private String orgName;

    @NotNull
    @Size(max = 20)
    private String state;

    @NotNull
    @Size(max = 20)
    private String actype;

    @Size(max = 140)
    private String dataRole1;

    @Size(max = 140)
    private String dataRole2;

    @Size(max = 140)
    private String secuLayer;

    private Instant expirationTime;

    @NotNull
    private Instant createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessTokenName() {
        return accessTokenName;
    }

//    public void setAccessTokenName(String accessTokenName) {
//        this.accessTokenName = accessTokenName;
//    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getActype() {
        return actype;
    }

    public void setActype(String actype) {
        this.actype = actype;
    }

    public String getDataRole1() {
        return dataRole1;
    }

    public void setDataRole1(String dataRole1) {
        this.dataRole1 = dataRole1;
    }

    public String getDataRole2() {
        return dataRole2;
    }

    public void setDataRole2(String dataRole2) {
        this.dataRole2 = dataRole2;
    }

    public String getSecuLayer() {
        return secuLayer;
    }

    public void setSecuLayer(String secuLayer) {
        this.secuLayer = secuLayer;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccessTokenDTO)) {
            return false;
        }

        AccessTokenDTO accessTokenDTO = (AccessTokenDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, accessTokenDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AccessTokenDTO{" +
            "id=" + getId() +
            ", accessToken='" + getAccessToken() + "'" +
            ", accessTokenName='" + getAccessTokenName() + "'" +
            ", owner='" + getOwner() + "'" +
            ", ownerName='" + getOwnerName() + "'" +
            ", orgId='" + getOrgId() + "'" +
            ", orgName='" + getOrgName() + "'" +
            ", state='" + getState() + "'" +
            ", actype='" + getActype() + "'" +
            ", dataRole1='" + getDataRole1() + "'" +
            ", dataRole2='" + getDataRole2() + "'" +
            ", secuLayer='" + getSecuLayer() + "'" +
            ", expirationTime='" + getExpirationTime() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            "}";
    }
}
