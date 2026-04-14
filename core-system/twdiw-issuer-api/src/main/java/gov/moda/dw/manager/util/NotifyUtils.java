package gov.moda.dw.manager.util;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import gov.moda.dw.manager.service.custom.AmsMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** 通用 發送通知 工具 (E-mail、LINE Notify) */
@Slf4j
@Component
public class NotifyUtils {

  @Autowired
  private AmsMailService mail;

  public static NotifyUtils notifyUtils;

  @PostConstruct
  public void init() {
    notifyUtils = this;
    notifyUtils.mail = this.mail;
  }

  /**
   * 寄送內容 by mail
   * @param msg 訊息內容
   * @return 新郵件
   */
  public static MailBuilder mail(String msg) {
    return new MailBuilder(msg);
  }

  /**
   * 寄送內容 by mail
   * @return 新郵件
   */
  public static MailBuilder mail() {
    return new MailBuilder();
  }

  /**
   * E-mail Builder 郵件與發送
   */
  public static class MailBuilder {

    private String message;
    private String subject;
    private List<String> to;
    private Boolean html;
    private List<String> file;
    private Map<String, String> imagePaths;

    /**
     * 初始化新郵件
     */
    public MailBuilder() {
      this.message = "";
      this.subject = "";
      this.to = new ArrayList<>();
      this.html = false;
      this.file = new ArrayList<>();
      this.imagePaths = new HashMap<>();
    }

    /**
     * 初始化新郵件
     * @param message 設定訊息
     */
    public MailBuilder(String message) {
      this.message = Optional.ofNullable(message).orElse("");
      this.subject = "";
      this.to = new ArrayList<>();
      this.html = false;
      this.file = new ArrayList<>();
      this.imagePaths = new HashMap<>();
    }

    /**
     * 設定 訊息
     * @param message 訊息
     * @return 郵件
     */
    public MailBuilder setMessage(String message) {
      this.message = Optional.ofNullable(message).orElse("");
      return this;
    }

    /**
     * 設定 主旨/標題
     * @param subject 主旨/標題
     * @return 郵件
     */
    public MailBuilder setSubject(String subject) {
      this.subject = Optional.ofNullable(subject).orElse("");
      return this;
    }

    /**
     * 加入 聯絡地址
     * @param mailAddress 聯絡地址
     * @return 郵件
     */
    public MailBuilder addContact(String mailAddress) {
      if (mailAddress != null) this.to.add(mailAddress);
      return this;
    }

    /**
     * 加入 多個聯絡地址
     * @param mailAddress 聯絡地址
     * @return 郵件
     */
    public MailBuilder addContact(String... mailAddress) {
      if (mailAddress != null) this.to.addAll(new ArrayList<>(Arrays.asList(mailAddress)));
      return this;
    }

    /**
     * 加入 所有聯絡地址
     * @param mailAddresses 聯絡地址
     * @return 郵件
     */
    public MailBuilder addAllContact(Collection<? extends String> mailAddresses) {
      if (mailAddresses != null) this.to.addAll(mailAddresses);
      return this;
    }

    /**
     * 改訊息為 html 格式
     * @return 郵件
     */
    public MailBuilder html() {
      this.html = true;
      return this;
    }

    /**
     * @param isHtml boolean判斷訊息是否為html格式
     * @return 郵件
     */
    public MailBuilder html(boolean isHtml) {
      this.html = isHtml;
      return this;
    }

    /**
     * 加入 附件
     * @param file 附件
     * @return 郵件
     */
    public MailBuilder addFile(String file) {
      if (file != null) this.file.add(file);
      return this;
    }

    /**
     * 加入 多個附件
     * @param file 附件
     * @return 郵件
     */
    public MailBuilder addFile(String... file) {
      if (file != null) this.file.addAll(new ArrayList<>(Arrays.asList(file)));
      return this;
    }

    /**
     * 加入 所有附件
     * @param files 附件
     * @return 郵件
     */
    public MailBuilder addAllFile(Collection<? extends String> files) {
      if (files != null) this.file.addAll(files);
      return this;
    }

    /**
     * 加入 所有內嵌圖片
     * @param imagePaths 內嵌圖片
     * @return 郵件
     */
    public MailBuilder addAllImagePaths(Map<String, String> imagePaths) {
      if (imagePaths != null) this.imagePaths = imagePaths;
      return this;
    }

    /**
     * 寄送郵件
     * @return true:成功、false:失敗
     */
    public Boolean send() {
      if (null == this.to || 0 == this.to.size()) {
        log.error("No contact to send mail");
        return false;
      }

      if (this.html || (null != this.file && 0 != this.file.size())) { // Mime msg
        return notifyUtils.mail.sendAttachment(this.subject, this.message, this.to, this.html, this.file, this.imagePaths);
      } else { // simple msg
        return notifyUtils.mail.send(this.subject, this.message, this.to);
      }
    }
  }
}
