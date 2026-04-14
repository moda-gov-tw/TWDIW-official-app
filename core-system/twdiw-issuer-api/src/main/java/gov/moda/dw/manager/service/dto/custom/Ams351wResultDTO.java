package gov.moda.dw.manager.service.dto.custom;

import java.io.Serializable;
import lombok.Data;
import gov.moda.dw.manager.service.dto.AccessTokenDTO;
import gov.moda.dw.manager.type.StatusCode;

@Data
public class Ams351wResultDTO implements Serializable {

  private StatusCode statusCode;

  private Ams351wAccessTokenResDTO ams351wAccessTokenResDTO;

  public Ams351wResultDTO(StatusCode statusCode) {
    this.statusCode = statusCode;
  }

  public Ams351wResultDTO(Ams351wAccessTokenResDTO ams351wAccessTokenResDTO) {
    this.ams351wAccessTokenResDTO = ams351wAccessTokenResDTO;
    setStatusCode(StatusCode.SUCCESS);
  }

  public Ams351wResultDTO(AccessTokenDTO accessTokenDTO) {
    this.ams351wAccessTokenResDTO = new Ams351wAccessTokenResDTO(accessTokenDTO);
    setStatusCode(StatusCode.SUCCESS);
  }
}
