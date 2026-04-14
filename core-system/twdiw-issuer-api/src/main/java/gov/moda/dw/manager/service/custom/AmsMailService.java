package gov.moda.dw.manager.service.custom;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import jakarta.annotation.PostConstruct;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation for mail.
 */
@Slf4j
@Service
@Transactional
public class AmsMailService {

  private static final String ENTITY_NAME = "AmsMailService";

  private AmsMailService mailService;

  @Autowired
  private JavaMailSender javaMailSender;

  @Value("${spring.mail.username:}")
  public String username;

  @Value("${spring.mail.nickname:發行端模組系統}")
  public String nickname;

  @Value("${mail.from.address:}")
  public String fromAddress;

  @PostConstruct
  public void init() {
    mailService = this;
    mailService.javaMailSender = this.javaMailSender;
    mailService.username = this.username;
  }

  /**
   * 簡單 郵件 (純文字訊息)
   * @param subject 主旨
   * @param message 訊息
   * @param to 收件人
   * @return true:成功、false:失敗
   */
  public Boolean send(String subject, String message, Collection<? extends String> to) {
    SimpleMailMessage mail = new SimpleMailMessage();
    String mailFrom = this.fromAddress;
    if (StringUtils.isBlank(mailFrom)) {
        mailFrom = this.username;
    }

    log.debug("[{}] " + "送件人:{}", ENTITY_NAME, mailFrom);
    log.debug("[{}] " + "主旨:{}", ENTITY_NAME, subject);
    log.debug("[{}] " + "發送內容:{}", ENTITY_NAME, message);
    log.debug("[{}] " + "收件人:{}", ENTITY_NAME, to);

    mail.setTo(to.toArray(String[]::new));
    mail.setSubject(sanitizeSubject(subject));
    mail.setText(message);
    mail.setFrom(mailFrom);

    try {
      javaMailSender.send(mail);
    } catch (MailAuthenticationException e) {
      log.error("[{}] " + ExceptionUtils.getStackTrace(e), ENTITY_NAME);
      log.error("[{}] " + "寄件伺服器登入失敗，請檢查設定:{}", ENTITY_NAME, mail);
      return false;
    } catch (MailException e) {
      log.error("[{}] " + ExceptionUtils.getStackTrace(e), ENTITY_NAME);
      log.error("[{}] " + "郵件發送失敗:{}", ENTITY_NAME, mail);
      return false;
    } catch (Exception e) {
      log.error("[{}] " + ExceptionUtils.getStackTrace(e), ENTITY_NAME);
      log.error("[{}] " + "例外:{}", ENTITY_NAME, e.getMessage());
      return false;
    }

    log.info("[{}] " + "送件結束", ENTITY_NAME);
    return true;
  }

  /**
   * Mime 格式 郵件 (html、附件)
   * @param subject 主旨
   * @param message 訊息
   * @param to 收件人
   * @param html html格式 內文設置 true:text/html、false:text/plain
   * @param file 附件
   * @param imagePaths 內嵌圖片
   * @return true:成功、false:失敗
   */
  public Boolean sendAttachment(
    String subject,
    String message,
    Collection<? extends String> to,
    Boolean html,
    Collection<? extends String> file,
    Map<String, String> imagePaths
  ) {
    List<Boolean> issue = new ArrayList<>();
    MimeMessage mail = javaMailSender.createMimeMessage();

    try {
      MimeMessageHelper helper = new MimeMessageHelper(mail, true, StandardCharsets.UTF_8.name());
      String mailFrom = this.fromAddress;
      if (StringUtils.isBlank(mailFrom)) {
          mailFrom = this.username;
      }

      log.debug("[{}] " + "送件人:{}/{}", ENTITY_NAME, this.nickname, mailFrom);
      log.debug("[{}] " + "主旨:{}", ENTITY_NAME, subject);
      log.debug("[{}] " + "發送內容:{}", ENTITY_NAME, message);
      log.debug("[{}] " + "收件人:{}", ENTITY_NAME, to);
      log.debug("[{}] " + "HTML格式:{}", ENTITY_NAME, html);
      log.debug("[{}] " + "附件:{}", ENTITY_NAME, file);

      if (StringUtils.isBlank(this.nickname)) {
          helper.setFrom(mailFrom);
      } else {
          helper.setFrom(new InternetAddress(mailFrom, this.nickname, StandardCharsets.UTF_8.name()));
      }
      helper.setTo(to.stream().toArray(String[]::new));
      helper.setSubject(sanitizeSubject(subject));
      helper.setText(message, html);
      /* attachment start */
      file.forEach(x -> {
        try {
          File attachment = ResourceUtils.getFile(x);
          String fileName = x.substring(x.lastIndexOf(File.separator) + 1);
          helper.addAttachment(fileName, attachment);
        } catch (FileNotFoundException | NullPointerException e) {
          log.error("[{}] " + ExceptionUtils.getStackTrace(e), ENTITY_NAME);
          log.error("[{}] " + "找不到檔案:{}", ENTITY_NAME, x);
          issue.add(true);
        } catch (MessagingException e) {
          log.error("[{}] " + ExceptionUtils.getStackTrace(e), ENTITY_NAME);
          log.error("[{}] " + "夾帶檔案失敗:{}", ENTITY_NAME, x);
          issue.add(true);
        }
      });

      // 內嵌圖片處理
      for (Map.Entry<String, String> entry : imagePaths.entrySet()) {
          String cid = entry.getKey();
          String imgPath = entry.getValue();

          helper.addInline(cid, new ClassPathResource(imgPath));
      }

      log.debug("[{}] " + "issue.size() = {}", ENTITY_NAME, issue.size());
      if (issue.stream().anyMatch(x -> x == true)) return false;
      /* attachment end */

      javaMailSender.send(mail);
    } catch (AuthenticationFailedException | MailAuthenticationException e) {
      log.error("[{}] " + ExceptionUtils.getStackTrace(e), ENTITY_NAME);
      log.error("[{}] " + "寄件伺服器登入失敗，請檢查設定:{}", ENTITY_NAME, mail.toString());
      return false;
    } catch (MessagingException e) {
      log.error("[{}] " + ExceptionUtils.getStackTrace(e), ENTITY_NAME);
      log.error("[{}] " + "郵件發送失敗:{}", ENTITY_NAME, mail.toString());
      return false;
    } catch (Exception e) {
      log.error("[{}] " + ExceptionUtils.getStackTrace(e), ENTITY_NAME);
      log.error("[{}] " + "例外:{}", ENTITY_NAME, e.getMessage());
      return false;
    }
    log.info("[{}] " + "送件結束", ENTITY_NAME);
    return true;
  }

  /**
   * 移除 CR/LF，避免 Header Injection
   * 
   * @param subject
   * @return
   */
  private String sanitizeSubject(String subject) {
      if (StringUtils.isBlank(subject)) {
          return "";
      }

      return subject.replaceAll("[\\r\\n]", "");
  }

}
