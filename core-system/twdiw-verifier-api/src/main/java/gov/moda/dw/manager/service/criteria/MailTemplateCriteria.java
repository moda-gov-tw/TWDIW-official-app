package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MailTemplateCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter mailType;

    private StringFilter description;

    private StringFilter recipientRole;

    private StringFilter subject;

    private StringFilter htmlState;

    private StringFilter activated;

    private InstantFilter createTime;

    private Boolean distinct;

    public MailTemplateCriteria() {}

    public MailTemplateCriteria(MailTemplateCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.mailType = other.optionalMailType().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.recipientRole = other.optionalRecipientRole().map(StringFilter::copy).orElse(null);
        this.subject = other.optionalSubject().map(StringFilter::copy).orElse(null);
        this.htmlState = other.optionalHtmlState().map(StringFilter::copy).orElse(null);
        this.activated = other.optionalActivated().map(StringFilter::copy).orElse(null);
        this.createTime = other.optionalCreateTime().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public MailTemplateCriteria copy() {
        return new MailTemplateCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getMailType() {
        return mailType;
    }

    public Optional<StringFilter> optionalMailType() {
        return Optional.ofNullable(mailType);
    }

    public StringFilter mailType() {
        if (mailType == null) {
            setMailType(new StringFilter());
        }
        return mailType;
    }

    public void setMailType(StringFilter mailType) {
        this.mailType = mailType;
    }

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getRecipientRole() {
        return recipientRole;
    }

    public Optional<StringFilter> optionalRecipientRole() {
        return Optional.ofNullable(recipientRole);
    }

    public StringFilter recipientRole() {
        if (recipientRole == null) {
            setRecipientRole(new StringFilter());
        }
        return recipientRole;
    }

    public void setRecipientRole(StringFilter recipientRole) {
        this.recipientRole = recipientRole;
    }

    public StringFilter getSubject() {
        return subject;
    }

    public Optional<StringFilter> optionalSubject() {
        return Optional.ofNullable(subject);
    }

    public StringFilter subject() {
        if (subject == null) {
            setSubject(new StringFilter());
        }
        return subject;
    }

    public void setSubject(StringFilter subject) {
        this.subject = subject;
    }

    public StringFilter getHtmlState() {
        return htmlState;
    }

    public Optional<StringFilter> optionalHtmlState() {
        return Optional.ofNullable(htmlState);
    }

    public StringFilter htmlState() {
        if (htmlState == null) {
            setHtmlState(new StringFilter());
        }
        return htmlState;
    }

    public void setHtmlState(StringFilter htmlState) {
        this.htmlState = htmlState;
    }

    public StringFilter getActivated() {
        return activated;
    }

    public Optional<StringFilter> optionalActivated() {
        return Optional.ofNullable(activated);
    }

    public StringFilter activated() {
        if (activated == null) {
            setActivated(new StringFilter());
        }
        return activated;
    }

    public void setActivated(StringFilter activated) {
        this.activated = activated;
    }

    public InstantFilter getCreateTime() {
        return createTime;
    }

    public Optional<InstantFilter> optionalCreateTime() {
        return Optional.ofNullable(createTime);
    }

    public InstantFilter createTime() {
        if (createTime == null) {
            setCreateTime(new InstantFilter());
        }
        return createTime;
    }

    public void setCreateTime(InstantFilter createTime) {
        this.createTime = createTime;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MailTemplateCriteria that = (MailTemplateCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(mailType, that.mailType) &&
            Objects.equals(description, that.description) &&
            Objects.equals(recipientRole, that.recipientRole) &&
            Objects.equals(subject, that.subject) &&
            Objects.equals(htmlState, that.htmlState) &&
            Objects.equals(activated, that.activated) &&
            Objects.equals(createTime, that.createTime) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mailType, description, recipientRole, subject, htmlState, activated, createTime, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MailTemplateCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalMailType().map(f -> "mailType=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalRecipientRole().map(f -> "recipientRole=" + f + ", ").orElse("") +
            optionalSubject().map(f -> "subject=" + f + ", ").orElse("") +
            optionalHtmlState().map(f -> "htmlState=" + f + ", ").orElse("") +
            optionalActivated().map(f -> "activated=" + f + ", ").orElse("") +
            optionalCreateTime().map(f -> "createTime=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
            "}";
    }
}
