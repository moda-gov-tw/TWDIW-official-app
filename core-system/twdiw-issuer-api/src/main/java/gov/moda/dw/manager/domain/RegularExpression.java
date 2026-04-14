package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A RegularExpression.
 */
@Entity
@Table(name = "regular_expression")
public class RegularExpression implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * 正規表達式執行類型(Require所有欄位都要檢查|Specified指定欄位檢查)
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "type", length = 50, nullable = false)
    private String type;

    /**
     * 正規表達式名稱
     */
    @NotNull
    @Size(max = 40)
    @Column(name = "name", length = 40, nullable = false)
    private String name;

    /**
     * 正規表達式
     */
    @NotNull
    @Size(max = 200)
    @Column(name = "regular_expression", length = 200, nullable = false)
    private String regularExpression;

    /**
     * 描述
     */
    @NotNull
    @Size(max = 200)
    @Column(name = "description", length = 200, nullable = false)
    private String description;

    /**
     * 返回給使用者用的錯誤訊息
     */
    @NotNull
    @Size(max = 200)
    @Column(name = "error_msg", length = 200, nullable = false)
    private String errorMsg;

    /**
     * 正面表列或反面表列
     */
    @NotNull
    @Size(max = 10)
    @Column(name = "rule_type", length = 10, nullable = false)
    private String ruleType;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RegularExpression id(Long id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return this.type;
    }

    public RegularExpression type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public RegularExpression name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegularExpression() {
        return this.regularExpression;
    }

    public RegularExpression regularExpression(String regularExpression) {
        this.regularExpression = regularExpression;
        return this;
    }

    public void setRegularExpression(String regularExpression) {
        this.regularExpression = regularExpression;
    }

    public String getDescription() {
        return this.description;
    }

    public RegularExpression description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public RegularExpression errorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getRuleType() {
        return this.ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegularExpression)) {
            return false;
        }
        return id != null && id.equals(((RegularExpression) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RegularExpression{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", name='" + getName() + "'" +
            ", regularExpression='" + getRegularExpression() + "'" +
            ", description='" + getDescription() + "'" +
            ", errorMsg='" + getErrorMsg() + "'" +
            ", ruleType='" + getRuleType() + "'" +
            "}";
    }
}
