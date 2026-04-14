package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "建立 VC DATA 用")
public class CreateVCItemDataField {

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    @Schema(description = "欄位對外名稱(中/英)")
    private String cname;

    @Schema(description = "欄位名稱(英)")
    private String ename;

    @Schema(description = "欄位的值")
    // 預設是空字串(這是談好的規格，請勿異動)
    private String content="";
}
