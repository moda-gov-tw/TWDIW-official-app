package gov.moda.dw.manager.service.custom;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.manager.domain.BwdParam;
import gov.moda.dw.manager.domain.ExtendedUser;
import gov.moda.dw.manager.domain.LoginCount;
import gov.moda.dw.manager.domain.User;
import gov.moda.dw.manager.repository.ExtendedUserRepository;
import gov.moda.dw.manager.repository.LoginCountRepository;
import gov.moda.dw.manager.repository.UserRepository;
import gov.moda.dw.manager.service.BwdHistoryService;
import gov.moda.dw.manager.service.dto.BwdHistoryDTO;
import gov.moda.dw.manager.service.dto.ExtendedUserDTO;
import gov.moda.dw.manager.service.dto.custom.Ams302wValidateResetKetResDTO;
import gov.moda.dw.manager.service.mapper.ExtendedUserMapper;
import gov.moda.dw.manager.type.BwdProfileType;
import gov.moda.dw.manager.web.rest.vm.ManagedUserVM;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class Ams302wService {

  @Autowired
  private LoginCountRepository loginCountRepository;

  @Autowired
  private BwdParamCustomService bwdParamCustomService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ExtendedUserRepository extendedUserRepository;

  @Autowired
  private BwdHistoryService bwdHistoryService;

  @Autowired
  private ExtendedUserMapper extendedUserMapper;

  /**
   * 取得 UserId、UserName 資訊
   * @param resetKey 重置key
   * @return
   */
  public Ams302wValidateResetKetResDTO getUserIdAndName(String resetKey) {
      Ams302wValidateResetKetResDTO ams302wValidateResetKetResDTO = null;
      Optional<User> optionalUser = this.getResetUser(resetKey);
      if (optionalUser.isPresent()) {
          Optional<ExtendedUser> extendedUserOp = extendedUserRepository
                  .findOneByUserId(optionalUser.get().getLogin());
          if (extendedUserOp.isEmpty()) {
              log.info("Ams302wService-getExtendedUser Extended User table 找不到該筆資料");
              return ams302wValidateResetKetResDTO;
          }
          ExtendedUserDTO extendedUserDTO = extendedUserMapper.toDto(extendedUserOp.get());
          ams302wValidateResetKetResDTO = new Ams302wValidateResetKetResDTO();
          ams302wValidateResetKetResDTO.setUserId(extendedUserDTO.getUserId());
          ams302wValidateResetKetResDTO.setUserName(extendedUserDTO.getUserName());
      } else {
          log.info("Ams302wService-getExtendedUser User table 找不到該筆資料 resetKey={}", resetKey);
      }

      return ams302wValidateResetKetResDTO;
  }

  /**
   * 重置key 的合法性
   * @param resetKey 重置key
   * @return
   */
  public Optional<User> getResetUser(String resetKey) {
    return this.userRepository.findOneByResetKey(resetKey).filter(this::resetKeyNotExpired);
  }

  /**
   * 檢查 重置key 時間有效性
   * (一日內)
   * @param user
   * @return
   */
  private boolean resetKeyNotExpired(User user) {
    Instant resetDate = user.getResetDate();
    return Instant.now().isBefore(resetDate.plus(1440, ChronoUnit.MINUTES));
  }

  public void finishResetBwd(String newBwd, String resetKey) {
    log.info("完成重設user密碼，由重置key: {}", resetKey);

    User user = this.getResetUser(resetKey).orElseThrow(() -> new RuntimeException("沒有根據重置key找到對應使用者"));

    // 重置 使用者登入失敗次數
    String login = user.getLogin();
    this.resetLoginCount(login, false);

    // 檢查密碼原則
    this.checkBwdRule(login, newBwd, true);

    // 清空 帳號的重置資訊
    user.setPassword(this.passwordEncoder.encode(newBwd));
    user.setResetKey(null);
    user.setResetDate(null);
    user.setLastModifiedBy(login);
    user.setLastModifiedDate(Instant.now());

    this.userRepository.save(user);
  }

  private void checkBwdRule(String userId, String newBwd, boolean isUrlReset) {
    // 取得 密碼原則
    List<BwdParam> rules = this.bwdParamCustomService.getBwdRule(BwdProfileType.DEFAULT, false);

    Map<String, BwdParam> ruleIdMap = new HashMap<>();
    for (BwdParam bwdParam : rules) {
      ruleIdMap.put(bwdParam.getRuleId(), bwdParam);
    }

    // 檢查 及 取得錯誤訊息
    String msg = this.bwdParamCustomService.ruleCheck(userId, newBwd, ruleIdMap, isUrlReset, false);

    if (null != msg) {
      throw new RuntimeException(msg);
    } else {
      String cipher = this.passwordEncoder.encode(newBwd);
      this.logBwdHistory(userId, cipher);
    }
  }

  /**
   * 存到密碼紀錄當log
   * @param userId
   * @param encodeBwd
   */
  private void logBwdHistory(String userId, String encodeBwd) {
    BwdHistoryDTO bwdHistoryDTO = new BwdHistoryDTO();
    bwdHistoryDTO.setUserId(userId);
    bwdHistoryDTO.setBwdHash(encodeBwd);
    bwdHistoryDTO.setCreateTime(Instant.now());
    this.bwdHistoryService.save(bwdHistoryDTO);
  }

  /**
   * 重置 使用者登入失敗次數(loginCount, failCount)
   * @param userId
   * @param isForce (是否強制導頁至「更改密碼」)
   * @return
   */
  public LoginCount resetLoginCount(String userId, boolean isForce) {
    LoginCount loginCount =
      this.loginCountRepository.findOneByUserId(userId).orElse(new LoginCount().userId(userId).failCount(0).updateTime(Instant.now()));

    loginCount.setFailCount(isForce ? -1 : 0);
    return this.loginCountRepository.save(loginCount);
  }

  /**
   * 檢查 密碼 長度
   * @param bwd
   * @return
   */
  public boolean checkBwdLength(String bwd) {
    return (
      StringUtils.isNotEmpty(bwd) && bwd.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH && bwd.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH
    );
  }

  public User getChangeUser(String currentBwd, String login) {
    User user = this.userRepository.findOneByLogin(login).orElseThrow(() -> new RuntimeException("帳號密碼錯誤"));
    if (!this.passwordEncoder.matches(user.getPassword(), currentBwd)) {
      throw new RuntimeException("密碼輸入不正確");
    }

    return user;
  }

  /**
   * 完成 變更 密碼
   * @param user
   * @param newBwd
   */
  public void finishChangeBwd(User user, String newBwd) {
    String cipher = this.passwordEncoder.encode(newBwd);

    // 檢查密碼原則
    this.checkBwdRule(user.getLogin(), newBwd, false);

    // 變更 帳號 密碼、清除 重置key
    user.setPassword(cipher);
    user.setResetKey(null);
    user.setResetDate(null);
    user.setLastModifiedBy(user.getLogin());
    user.setLastModifiedDate(Instant.now());
    this.userRepository.save(user);
  }
}
