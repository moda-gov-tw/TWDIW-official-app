package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ams321wReqDTO {

    @JsonProperty("id")
    private Long id; // 角色 table id

    @JsonProperty("roleId")
    private String categoryId; // 角色代碼

    @JsonProperty("roleName")
    private String categoryName; // 角色名稱

    @JsonProperty("description")
    private String description; // 描述

    @JsonProperty("state")
    private String state; // 啟用狀態

}
