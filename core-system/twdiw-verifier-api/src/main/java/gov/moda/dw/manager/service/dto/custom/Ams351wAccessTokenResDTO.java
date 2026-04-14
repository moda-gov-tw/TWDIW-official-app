package gov.moda.dw.manager.service.dto.custom;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import gov.moda.dw.manager.domain.AccessToken;
import gov.moda.dw.manager.service.dto.AccessTokenDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ams351wAccessTokenResDTO implements Serializable {

  private Long id;

  private String accessToken;

  private String accessTokenName;

  private String owner;

  private String ownerName;

  private String orgId;

  private String orgName;

  private String state;

  private String dataRole1;

  private Instant createTime;

  public Ams351wAccessTokenResDTO(AccessToken accessToken) {
    this.id = accessToken.getId();
    this.accessToken = accessToken.getAccessToken();
    this.accessTokenName = accessToken.getAccessTokenName();
    this.owner = accessToken.getOwner();
    this.ownerName = accessToken.getOwnerName();
    this.orgId = accessToken.getOrgId();
    this.orgName = accessToken.getOrgName();
    this.state = accessToken.getState().equals("enabled") ? "true" : "false";
    this.dataRole1 = accessToken.getDataRole1();
    this.createTime = accessToken.getCreateTime();
  }

  public Ams351wAccessTokenResDTO(AccessTokenDTO accessTokenDTO) {
    this.id = accessTokenDTO.getId();
    this.accessToken = accessTokenDTO.getAccessToken();
    this.accessTokenName = accessTokenDTO.getAccessTokenName();
    this.owner = accessTokenDTO.getOwner();
    this.ownerName = accessTokenDTO.getOwnerName();
    this.orgId = accessTokenDTO.getOrgId();
    this.orgName = accessTokenDTO.getOrgName();
    this.state = accessTokenDTO.getState().equals("enabled") ? "true" : "false";
    this.dataRole1 = accessTokenDTO.getDataRole1();
    this.createTime = accessTokenDTO.getCreateTime();
  }
}
