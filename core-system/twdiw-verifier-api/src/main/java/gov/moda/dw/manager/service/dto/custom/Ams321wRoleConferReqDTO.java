package gov.moda.dw.manager.service.dto.custom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ams321wRoleConferReqDTO {

    @JsonProperty("resList")
    private List<Ams321wConferRes> resList; // 準備賦予的功能權限

    @JsonProperty("id")
    private Long id; // 角色table的 id

    @JsonProperty("roleId")
    private String categoryId; // 角色table的 角色代碼

}
