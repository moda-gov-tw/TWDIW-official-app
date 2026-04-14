package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.Org} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrgDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    @Schema(description = "組織代碼", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orgId;

    @NotNull
    @Size(max = 100)
    @Schema(description = "組織中文名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orgTwName;

    @NotNull
    @Size(max = 100)
    @Schema(description = "組織英文名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orgEnName;

    @Schema(description = "建立日期")
    private Instant createTime;

    @Schema(description = "更新日期")
    private Instant updateTime;

    @Schema(description = "組織 Logo (正方形 base64)")
    @Lob
    private String logoSquare;

    @Schema(description = "組織 Logo (長方形 base64)")
    @Lob
    private String logoRectangle;
    
    private Boolean isDidOrg;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgTwName() {
        return orgTwName;
    }

    public void setOrgTwName(String orgTwName) {
        this.orgTwName = orgTwName;
    }

    public String getOrgEnName() {
        return orgEnName;
    }

    public void setOrgEnName(String orgEnName) {
        this.orgEnName = orgEnName;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    public String getLogoSquare() {
        return logoSquare;
    }

    public void setLogoSquare(String logoSquare) {
        this.logoSquare = logoSquare;
    }

    public String getLogoRectangle() {
        return logoRectangle;
    }

    public void setLogoRectangle(String logoRectangle) {
        this.logoRectangle = logoRectangle;
    }

    public Boolean getIsDidOrg() {
        return isDidOrg;
    }

    public void setIsDidOrg(Boolean isDidOrg) {
        this.isDidOrg = isDidOrg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrgDTO)) {
            return false;
        }

        OrgDTO orgDTO = (OrgDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orgDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrgDTO{" +
            "id=" + getId() +
            ", orgId='" + getOrgId() + "'" +
            ", orgTwName='" + getOrgTwName() + "'" +
            ", orgEnName='" + getOrgEnName() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", updateTime='" + getUpdateTime() + "'" +
            ", logoSquare='" + getLogoSquare() + "'" +
            ", logoRectangle='" + getLogoRectangle() + "'" +
            "}";
        }
}
