package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A MailTemplate.
 */
@Entity
@Table(name = "mail_template")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MailTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 信件類型
     */
    @NotNull
    @Size(max = 255)
    @Column(name = "mail_type", length = 255, nullable = false)
    private String mailType;

    /**
     * 描述
     */
    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    /**
     * 收件者角色
     */
    @NotNull
    @Size(max = 255)
    @Column(name = "recipient_role", length = 255, nullable = false)
    private String recipientRole;

    /**
     * 信件標題
     */
    @Size(max = 255)
    @Column(name = "subject", length = 255)
    private String subject;

    /**
     * 信件內容
     */
    //  @Lob
    @Column(name = "message", columnDefinition = "TEXT") // JHIPSTER BUG 需要增加columnDefinition = "TEXT"
    private String message;

    /**
     * 信件格式
     */
    @NotNull
    @Size(max = 10)
    @Column(name = "html_state", length = 10, nullable = false)
    private String htmlState;

    /**
     * 啟用狀態
     */
    @NotNull
    @Size(max = 10)
    @Column(name = "activated", length = 10, nullable = false)
    private String activated;

    /**
     * 建立日期
     */
    @NotNull
    @Column(name = "create_time", nullable = false)
    private Instant createTime;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MailTemplate id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMailType() {
        return this.mailType;
    }

    public MailTemplate mailType(String mailType) {
        this.setMailType(mailType);
        return this;
    }

    public void setMailType(String mailType) {
        this.mailType = mailType;
    }

    public String getDescription() {
        return this.description;
    }

    public MailTemplate description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecipientRole() {
        return this.recipientRole;
    }

    public MailTemplate recipientRole(String recipientRole) {
        this.setRecipientRole(recipientRole);
        return this;
    }

    public void setRecipientRole(String recipientRole) {
        this.recipientRole = recipientRole;
    }

    public String getSubject() {
        return this.subject;
    }

    public MailTemplate subject(String subject) {
        this.setSubject(subject);
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return this.message;
    }

    public MailTemplate message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHtmlState() {
        return this.htmlState;
    }

    public MailTemplate htmlState(String htmlState) {
        this.setHtmlState(htmlState);
        return this;
    }

    public void setHtmlState(String htmlState) {
        this.htmlState = htmlState;
    }

    public String getActivated() {
        return this.activated;
    }

    public MailTemplate activated(String activated) {
        this.setActivated(activated);
        return this;
    }

    public void setActivated(String activated) {
        this.activated = activated;
    }

    public Instant getCreateTime() {
        return this.createTime;
    }

    public MailTemplate createTime(Instant createTime) {
        this.setCreateTime(createTime);
        return this;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MailTemplate)) {
            return false;
        }
        return getId() != null && getId().equals(((MailTemplate) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MailTemplate{" +
            "id=" + getId() +
            ", mailType='" + getMailType() + "'" +
            ", description='" + getDescription() + "'" +
            ", recipientRole='" + getRecipientRole() + "'" +
            ", subject='" + getSubject() + "'" +
            ", message='" + getMessage() + "'" +
            ", htmlState='" + getHtmlState() + "'" +
            ", activated='" + getActivated() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            "}";
    }
}
