package gov.moda.dw.manager.service.custom;

import java.time.Instant;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.manager.domain.ExtendedUser;
import gov.moda.dw.manager.domain.User;
import gov.moda.dw.manager.repository.ExtendedUserRepository;
import gov.moda.dw.manager.repository.UserRepository;
import gov.moda.dw.manager.service.criteria.MailTemplateCriteria;
import gov.moda.dw.manager.service.dto.MailTemplateDTO;
import gov.moda.dw.manager.service.dto.custom.Ams301ReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams301wMailResultDTO;
import gov.moda.dw.manager.type.MailAction;
import gov.moda.dw.manager.type.StateType;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.NotifyUtils;
import gov.moda.dw.manager.util.StringFilterUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.jhipster.security.RandomUtil;

/**
 * Ams301wService
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class Ams301wService {

  private final UserRepository userRepository;

  private final ExtendedUserRepository extendedUserRepository;

  private final CustomMailTemplateQueryService mailTemplateQueryService;

  @Value("${jhipster.mail.base-url}")
  private String baseURL;

  /**
   * 發送重置信
   *
   * @param req
   * @return
   */
  public boolean doResetBwd(Ams301ReqDTO req) {
      User user = null;
      ExtendedUser exUser = null;
      // 因存入邏輯統一轉為小寫，故查詢時也需轉小寫
      if (StringUtils.isNotBlank(req.getMail())) {
          req.setMail(req.getMail().toLowerCase());
      }

      // 確認用戶是否存在
      user = this.userRepository.findOneByEmailIgnoreCase(req.getMail())
              .orElseThrow(() -> new RuntimeException("請求失敗，請確認輸入的信箱及聯絡電話是否正確，若有疑問，請洽系統管理員。"));

      // 確認用戶聯絡電話是否正確
      exUser = this.extendedUserRepository.findOneByUserId(req.getMail())
              .filter(x -> x.getTel().equals(req.getTel()))
              .orElseThrow(() -> new RuntimeException("請求失敗，請確認輸入的信箱及聯絡電話是否正確，若有疑問，請洽系統管理員。"));

      // 確認用戶是否已啟用
      if (!user.isActivated()) {
          throw new RuntimeException("此帳號尚未啟用，若有疑問，請洽系統管理員協助啟用帳號或重新發送帳號啟用信。");
      }

      try {

          String key = RandomUtil.generateResetKey();
          user.setResetKey(key);
          user.setResetDate(Instant.now());
          this.userRepository.save(user);
          return this.doResetMail(user, exUser);
      } catch (Exception e) {
          log.warn("請求重設密碼錯誤：{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
          throw new RuntimeException("請求失敗，請確認輸入的資訊是否正確。");
      }
  }

  /**
   * 發送 郵件
   *
   * @param user
   * @param exUser
   * @return true:成功、false:失敗
   */
  private boolean doResetMail(User user, ExtendedUser exUser) {
      log.debug("Ams301wService-doResetMail，準備發送信件，{}", MailAction.FORGOT_PASSWORD_RESET_BWD.getName());
    // 確認Mail範本。
    Ams301wMailResultDTO criteriaResult = this.queryMailTemplateByCriteria();
    if (!StatusCode.SUCCESS.getCode().equals(criteriaResult.getStatusCode().getCode())) {
      log.error("Ams301wService-doResetMail，寄送mail失敗，msg:{}", criteriaResult.getStatusCode().getMsg());
      throw new RuntimeException(criteriaResult.getStatusCode().getMsg());
    }

    MailTemplateDTO mailTemplateDTO = criteriaResult.getMailTemplateDTO();

    // 確認mailContent。
    String message = this.changeKeyWordForMessage(mailTemplateDTO.getMessage(), user, exUser);

    // 將寄信的參數帶入NotifyUtils。
    try {
      return NotifyUtils.mail()
        .addContact(exUser.getEmail())
        .setSubject(mailTemplateDTO.getSubject())
        .setMessage(message)
        .html(StateType.ENABLED.getCode().equals(mailTemplateDTO.getHtmlState()))
        .send();
    } catch (Exception ex) {
      log.error("Ams301wService-doSendMail，發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
      throw new RuntimeException(StatusCode.MAIL_UNKNOWN_ERROR.getMsg());
    }
  }

  private Ams301wMailResultDTO queryMailTemplateByCriteria() {
    MailAction mailAction = MailAction.FORGOT_PASSWORD_RESET_BWD;
    MailTemplateCriteria criteria = new MailTemplateCriteria();
    criteria.setMailType(StringFilterUtils.toEqualStringFilter(mailAction.getMailType()));
    criteria.setRecipientRole(StringFilterUtils.toEqualStringFilter(mailAction.getRecipient_role()));
    criteria.setActivated(StringFilterUtils.toEqualStringFilter(StateType.ENABLED.getCode()));

    // 撈出信件範本。
    List<MailTemplateDTO> templateDTOList = this.mailTemplateQueryService.findByCriteria(criteria);
    if (templateDTOList == null || templateDTOList.isEmpty()) {
      log.error("Ams301wService-queryMailTemplateByCriteria，發生錯誤，mailTemplate不存在，criteria:{}", criteria);
      return new Ams301wMailResultDTO(StatusCode.MAIL_TEMPLATE_NOT_EXISTS);
    }
    return new Ams301wMailResultDTO(templateDTOList.get(0));
  }

  /**
   * 替換MailBody關鍵字。
   */
  private String changeKeyWordForMessage(String content, User user, ExtendedUser extendedUser) {
    return content.replace("@userName", safeHtml(extendedUser.getUserName())).replace("@baseURL", this.baseURL).replace("@rest", user.getResetKey());
  }

  private String safeHtml(String input) {
      return Encode.forHtml(input == null ? "" : input);
  }
}
