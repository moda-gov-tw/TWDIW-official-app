package gov.moda.dw.manager.service.custom;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.manager.domain.AccessToken;
import gov.moda.dw.manager.repository.AccessTokenRepository;
import gov.moda.dw.manager.repository.custom.CustomAccessTokenRepository;
import gov.moda.dw.manager.security.accessToken.AccessTokenProvider;
import gov.moda.dw.manager.service.dto.custom.Ams351wReqDTO;
import gov.moda.dw.manager.service.mapper.AccessTokenMapper;
import gov.moda.dw.manager.type.AccessTokenType;
import gov.moda.dw.manager.type.AuthorityAction;
import gov.moda.dw.manager.type.StateType;

/**
 * Service Implementation for managing {@link AccessToken}.
 */
@Service
@Transactional
public class CustomAccessTokenService {

  private final Logger log = LoggerFactory.getLogger(CustomAccessTokenService.class);
  private final AccessTokenRepository accessTokenRepository;
  private final CustomAccessTokenRepository customAccessTokenRepository;
  private final AccessTokenMapper accessTokenMapper;
  private final AccessTokenProvider accessTokenProvider;

  public CustomAccessTokenService(
    AccessTokenRepository accessTokenRepository,
    AccessTokenMapper accessTokenMapper,
    AccessTokenProvider accessTokenProvider,
    CustomAccessTokenRepository customAccessTokenRepository
  ) {
    this.accessTokenRepository = accessTokenRepository;
    this.accessTokenMapper = accessTokenMapper;
    this.accessTokenProvider = accessTokenProvider;
    this.customAccessTokenRepository = customAccessTokenRepository;
  }

  /**
   * 建立AccessToken。
   */
  public AccessToken saveAccessToken(Ams351wReqDTO ams351wReqDTO) {
    try {
      Instant currentTime = this.getCurrentTimeInLocalZone();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.of("UTC"));

      AccessToken accessToken = new AccessToken();

      accessToken.setAccessToken(createUniqueAccessToken());
      accessToken.setAccessTokenName(ams351wReqDTO.getAuthKeyName());
      accessToken.setOrgId(ams351wReqDTO.getOrgId());
      accessToken.setOwnerName(ams351wReqDTO.getOwnerName());
      accessToken.setState(
        StateType.ENABLED.getEnName().equals(ams351wReqDTO.getState()) ? StateType.ENABLED.getEnName() : StateType.DISABLED.getEnName()
      );
      accessToken.setCreateTime(currentTime);
      accessToken.setDataRole1(formatter.format(currentTime));
      accessToken.setActype(AccessTokenType.UNLIMITED.getCode());

      AccessToken save = this.accessTokenRepository.save(accessToken);
      log.info("CustomAccessTokenService-saveAccessToken，建立AccessToken完成，accessToken:{}", save.getAccessToken());
      return save;
    } catch (Exception ex) {
      log.error("CustomAccessTokenService-saveAccessToken-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
      return null;
    }
  }

  /**
   * 更新AccessToken。
   */
  public AccessToken updateAccessToken(Ams351wReqDTO ams351wReqDTO, AuthorityAction actionType) {
    try {
      Instant currentTime = this.getCurrentTimeInLocalZone();

      Optional<AccessToken> dbAccessToken = this.accessTokenRepository.findById(ams351wReqDTO.getId());

      if (dbAccessToken.isPresent()) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.of("UTC"));
        String formattedDate = formatter.format(currentTime);

        if (actionType.equals(AuthorityAction.ACCESS_TOKEN_UPDATE_RES)) {
          dbAccessToken.get().setAccessTokenName(ams351wReqDTO.getAuthKeyName());
          dbAccessToken.get().setOwnerName(ams351wReqDTO.getOwnerName());
          dbAccessToken.get().setOrgId(ams351wReqDTO.getOrgId());
          dbAccessToken.get().setState("true".equals(ams351wReqDTO.getState()) ? "enabled" : "disabled");
        } else if (actionType.equals(AuthorityAction.ACCESS_TOKEN_CHANGE_ACTIVATED)) {
          dbAccessToken.get().setState("enabled".equals(dbAccessToken.get().getState()) ? "disabled" : "enabled");
        }
        dbAccessToken.get().setDataRole1(formattedDate);
        AccessToken update = this.accessTokenRepository.save(dbAccessToken.get());
        log.info("CustomAccessTokenService-updateAccessToken，更新AccessToken完成，accessToken:{}", update.getAccessToken());
        return update;
      }
    } catch (Exception ex) {
      log.error("CustomAccessTokenService-updateAccessToken-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
    }
    return null;
  }

  /**
   * 建立AccessToken(for CreateUser)。
   */
  public AccessToken saveAccessToken4CreateUser(Ams351wReqDTO ams351wReqDTO) {
    try {
      AccessToken accessToken = new AccessToken();
      accessToken.setAccessToken(createUniqueAccessToken());
      accessToken.setAccessTokenName(ams351wReqDTO.getAuthKeyName());
      accessToken.owner(ams351wReqDTO.getOwner());
      accessToken.setOrgId(ams351wReqDTO.getOrgId());
      accessToken.setOrgName(ams351wReqDTO.getOrgName());
      accessToken.setState(ams351wReqDTO.getState());
      accessToken.setCreateTime(this.getCurrentTimeInLocalZone());
      accessToken.setActype(ams351wReqDTO.getActype());

      AccessToken save = this.accessTokenRepository.save(accessToken);
      log.info("CustomAccessTokenService-saveAccessToken4CreateUser，建立AccessToken(for CreateUser)完成，accessToken:{}", save.getAccessToken());
      return save;
    } catch (Exception ex) {
      log.error("CustomAccessTokenService-saveAccessToken4CreateUser-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
      return null;
    }
  }

  /**
   * 刪除AccessToken
   * 
   * @param owner
   */
  public void deleteAccessToken(String owner) {
      try {
          List<AccessToken> ownerList = customAccessTokenRepository.findByOwner(owner);
          if (CollectionUtils.isNotEmpty(ownerList)) {
              customAccessTokenRepository.deleteAll(ownerList);
          }
      } catch (Exception ex) {
          log.error("CustomAccessTokenService-deleteAccessToken-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
      }
  }

  public Instant getCurrentTimeInLocalZone() {
    // 獲取當前的Instant。
    Instant now = Instant.now();
    // 定義本地時區(Asia/Taipei)。
    ZoneId localZoneId = ZoneId.of("Asia/Taipei");
    // 將Instant轉換為本地時區的ZonedDateTime
    ZonedDateTime localTime = now.atZone(localZoneId);
    // 返回本地時區的時間，轉換為Instant
    return localTime.toInstant();
  }

  // 確保產出的AccessToken是唯一的。
  public String createUniqueAccessToken() {
    String token;
    AccessToken existingToken;
    do {
      token = this.accessTokenProvider.createToken();
      existingToken = this.customAccessTokenRepository.findByAccessToken(token);
    } while (existingToken != null);
    return token;
  }
}
