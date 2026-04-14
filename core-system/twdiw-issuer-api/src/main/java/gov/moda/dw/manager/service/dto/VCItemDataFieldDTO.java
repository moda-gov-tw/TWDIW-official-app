package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class VCItemDataFieldDTO {

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }
    

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Schema(description = "欄位類型 BASIC|NORMAL|CUSTOM")
    private String type;

    @Schema(description = "是否必填")
    private Boolean isRequired;

    @Schema(description = "欄位對外名稱(中/英)")
    private String cname;

    @Schema(description = "欄位名稱(英)")
    private String ename;

    @Schema(description = "欄位的值")
    private String content;
}
