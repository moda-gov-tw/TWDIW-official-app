package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import gov.moda.dw.manager.annotation.RequiredField;
import gov.moda.dw.manager.annotation.ToMapDTO;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.SystemParam} entity.
 */
@ToMapDTO
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SystemParamDTO implements Serializable {

    @RequiredField
    private Long id;

    @Size(max = 255)
    @Schema(description = "參數類別")
    private String sysType;

    @Size(max = 255)
    @Schema(description = "參數代碼")
    @RequiredField
    private String sysCode;

    @Size(max = 255)
    @Schema(description = "參數名稱")
    private String sysName;

    @Size(max = 255)
    @Schema(description = "參數用途")
    private String sysEffect;

    @Size(max = 255)
    @Schema(description = "參數一")
    private String col1;

    @Size(max = 255)
    @Schema(description = "參數二")
    private String col2;

    @Size(max = 255)
    @Schema(description = "參數三")
    private String col3;

    @Size(max = 255)
    @Schema(description = "參數四")
    private String col4;

    @Size(max = 255)
    @Schema(description = "參數五")
    private String col5;

    @Size(max = 255)
    @Schema(description = "參數六")
    private String col6;

    @Size(max = 255)
    @Schema(description = "參數七")
    private String col7;

    @Size(max = 255)
    @Schema(description = "參數八")
    private String col8;

    @Size(max = 255)
    @Schema(description = "參數九")
    private String col9;

    @Size(max = 255)
    @Schema(description = "參數十")
    private String col10;

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

    public String getSysType() {
        return sysType;
    }

    public void setSysType(String sysType) {
        this.sysType = sysType;
    }

    public String getSysCode() {
        return sysCode;
    }

    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getSysEffect() {
        return sysEffect;
    }

    public void setSysEffect(String sysEffect) {
        this.sysEffect = sysEffect;
    }

    public String getCol1() {
        return col1;
    }

    public void setCol1(String col1) {
        this.col1 = col1;
    }

    public String getCol2() {
        return col2;
    }

    public void setCol2(String col2) {
        this.col2 = col2;
    }

    public String getCol3() {
        return col3;
    }

    public void setCol3(String col3) {
        this.col3 = col3;
    }

    public String getCol4() {
        return col4;
    }

    public void setCol4(String col4) {
        this.col4 = col4;
    }

    public String getCol5() {
        return col5;
    }

    public void setCol5(String col5) {
        this.col5 = col5;
    }

    public String getCol6() {
        return col6;
    }

    public void setCol6(String col6) {
        this.col6 = col6;
    }

    public String getCol7() {
        return col7;
    }

    public void setCol7(String col7) {
        this.col7 = col7;
    }

    public String getCol8() {
        return col8;
    }

    public void setCol8(String col8) {
        this.col8 = col8;
    }

    public String getCol9() {
        return col9;
    }

    public void setCol9(String col9) {
        this.col9 = col9;
    }

    public String getCol10() {
        return col10;
    }

    public void setCol10(String col10) {
        this.col10 = col10;
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
        if (!(o instanceof SystemParamDTO)) {
            return false;
        }

        SystemParamDTO systemParamDTO = (SystemParamDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, systemParamDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SystemParamDTO{" +
            "id=" + getId() +
            ", sysType='" + getSysType() + "'" +
            ", sysCode='" + getSysCode() + "'" +
            ", sysName='" + getSysName() + "'" +
            ", sysEffect='" + getSysEffect() + "'" +
            ", col1='" + getCol1() + "'" +
            ", col2='" + getCol2() + "'" +
            ", col3='" + getCol3() + "'" +
            ", col4='" + getCol4() + "'" +
            ", col5='" + getCol5() + "'" +
            ", col6='" + getCol6() + "'" +
            ", col7='" + getCol7() + "'" +
            ", col8='" + getCol8() + "'" +
            ", col9='" + getCol9() + "'" +
            ", col10='" + getCol10() + "'" +
            ", status='" + getStatus() + "'" +
            ", createID='" + getCreateID() + "'" +
            ", modifyTime='" + getModifyTime() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            "}";
    }
}
