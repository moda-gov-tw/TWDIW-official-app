package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import gov.moda.dw.manager.annotation.RequiredField;
import gov.moda.dw.manager.annotation.ToMapDTO;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.MemberPlatform} entity.
 */
@ToMapDTO
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MemberPlatformDTO implements Serializable {

    @RequiredField
    private Long id;

    @Size(max = 255)
    @Schema(description = "會員平台代碼")
    private String memberPlatformCode;

    @Size(max = 255)
    @Schema(description = "會員平台名稱")
    private String memberPlatformName;

    @Size(max = 255)
    @Schema(description = "負責人")
    private String owner;

    @Size(max = 255)
    @Schema(description = "負責人聯絡電話")
    private String ownerPhone;

    @Size(max = 255)
    @Schema(description = "負責人信箱")
    private String ownerEmail;

    @Size(max = 255)
    @Schema(description = "狀態")
    private String status;

    @Size(max = 255)
    @Schema(description = "建立者")
    private String createID;

    @Schema(description = "最後編輯時間")
    private Instant modifyTime;

    @Schema(description = "創建時間")
    private Instant createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMemberPlatformCode() {
        return memberPlatformCode;
    }

    public void setMemberPlatformCode(String memberPlatformCode) {
        this.memberPlatformCode = memberPlatformCode;
    }

    public String getMemberPlatformName() {
        return memberPlatformName;
    }

    public void setMemberPlatformName(String memberPlatformName) {
        this.memberPlatformName = memberPlatformName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateID() {
        return createID;
    }

    public void setCreateID(String createID) {
        this.createID = createID;
    }

    public Instant getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Instant modifyTime) {
        this.modifyTime = modifyTime;
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
        if (!(o instanceof MemberPlatformDTO)) {
            return false;
        }

        MemberPlatformDTO memberPlatformDTO = (MemberPlatformDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, memberPlatformDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MemberPlatformDTO{" +
            "id=" + getId() +
            ", memberPlatformCode='" + getMemberPlatformCode() + "'" +
            ", memberPlatformName='" + getMemberPlatformName() + "'" +
            ", owner='" + getOwner() + "'" +
            ", ownerPhone='" + getOwnerPhone() + "'" +
            ", ownerEmail='" + getOwnerEmail() + "'" +
            ", status='" + getStatus() + "'" +
            ", createID='" + getCreateID() + "'" +
            ", modifyTime='" + getModifyTime() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            "}";
    }
}
