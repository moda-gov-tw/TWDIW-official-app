package gov.moda.dw.manager.service.dto;

import gov.moda.dw.manager.domain.MailTemplate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link MailTemplate} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MailTemplateDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    @Schema(description = "信件類型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String mailType;

    @Size(max = 255)
    @Schema(description = "描述")
    private String description;

    @NotNull
    @Size(max = 255)
    @Schema(description = "收件者角色", requiredMode = Schema.RequiredMode.REQUIRED)
    private String recipientRole;

    @Size(max = 255)
    @Schema(description = "信件標題")
    private String subject;

    @Schema(description = "信件內容")
    //  @Lob
    @Column(columnDefinition = "TEXT") // JHIPSTER BUG 需要增加columnDefinition = "TEXT"
    private String message;

    @NotNull
    @Size(max = 10)
    @Schema(description = "信件格式", requiredMode = Schema.RequiredMode.REQUIRED)
    private String htmlState;

    @NotNull
    @Size(max = 10)
    @Schema(description = "啟用狀態", requiredMode = Schema.RequiredMode.REQUIRED)
    private String activated;

    @NotNull
    @Schema(description = "建立日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private Instant createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMailType() {
        return mailType;
    }

    public void setMailType(String mailType) {
        this.mailType = mailType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecipientRole() {
        return recipientRole;
    }

    public void setRecipientRole(String recipientRole) {
        this.recipientRole = recipientRole;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHtmlState() {
        return htmlState;
    }

    public void setHtmlState(String htmlState) {
        this.htmlState = htmlState;
    }

    public String getActivated() {
        return activated;
    }

    public void setActivated(String activated) {
        this.activated = activated;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MailTemplateDTO)) {
            return false;
        }

        MailTemplateDTO mailTemplateDTO = (MailTemplateDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mailTemplateDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MailTemplateDTO{" +
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
