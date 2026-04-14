package gov.moda.dw.manager.service.custom;

import java.time.Instant;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import gov.moda.dw.manager.domain.LoginCount;
import gov.moda.dw.manager.domain.User;
import gov.moda.dw.manager.repository.LoginCountRepository;
import gov.moda.dw.manager.repository.UserRepository;
import gov.moda.dw.manager.service.BwdHistoryService;
import gov.moda.dw.manager.service.CookiesMsgQueryService;
import gov.moda.dw.manager.service.CookiesMsgService;
import gov.moda.dw.manager.service.NonceQueryService;
import gov.moda.dw.manager.service.NonceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class CustomAuthenticateControllerService {

  @Autowired
  private LoginCountRepository loginCountRepository;

  @Autowired
  private BwdParamCustomService bwdParamCustomService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private CookiesMsgQueryService cookiesMsgQueryService;

  @Autowired
  private CookiesMsgService cookiesMsgService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BwdHistoryService bwdHistoryService;

  @Autowired
  private NonceQueryService nonceQueryService;

  @Autowired
  private NonceService nonceService;

  @Autowired
  private Ams302wService ams302wService;

  public void loginValidate(String userId, String currentBwd) {
    User user = null;
    try {
      user = this.ams302wService.getChangeUser(currentBwd, userId);
    } catch (Exception e) {
      log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));

      // 登入失敗：增加登入失敗次數
      if (StringUtils.contains(e.getMessage(), "密碼輸入不正確")) {
        this.increaseLoginCount(userId);
      }

      throw e;
    }

    this.ams302wService.resetLoginCount(user.getLogin(), false);
  }

  public void increaseLoginCount(String userId) {
    LoginCount cnt =
      this.loginCountRepository.findOneByUserId(userId).orElse(new LoginCount().userId(userId).failCount(0).updateTime(Instant.now()));

    this.loginCountRepository.save(cnt.failCount(cnt.getFailCount() + 1));
  }
}
