package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A Field.
 */
@Entity
@Table(name = "field")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Field implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 欄位對外名稱(中/英)
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "cname", length = 50, nullable = false)
    private String cname;

    /**
     * 欄位名稱(英)
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "ename", length = 50, nullable = false)
    private String ename;

    /**
     * 欄位類型
     */
    @NotNull
    @Size(max = 10)
    @Column(name = "type", length = 10, nullable = false)
    private String type;

    /**
     * 是否顯示
     */
    @NotNull
    @Column(name = "visible", nullable = false)
    private Boolean visible;

    /**
     * 單位
     */
    @NotNull
    @Size(max = 255)
    @Column(name = "business_id", length = 255, nullable = false)
    private String businessId;

    @OneToOne(cascade = {})
    @JoinColumn(unique = true)
    private RegularExpression regularExpression;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Field id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCname() {
        return this.cname;
    }

    public Field cname(String cname) {
        this.setCname(cname);
        return this;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getEname() {
        return this.ename;
    }

    public Field ename(String ename) {
        this.setEname(ename);
        return this;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getType() {
        return this.type;
    }

    public Field type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getVisible() {
        return this.visible;
    }

    public Field visible(Boolean visible) {
        this.setVisible(visible);
        return this;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getBusinessId() {
        return this.businessId;
    }

    public Field businessId(String businessId) {
        this.setBusinessId(businessId);
        return this;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Field)) {
            return false;
        }
        return getId() != null && getId().equals(((Field) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Field{" +
            "id=" + getId() +
            ", cname='" + getCname() + "'" +
            ", ename='" + getEname() + "'" +
            ", type='" + getType() + "'" +
            ", visible='" + getVisible() + "'" +
            ", businessId='" + getBusinessId() + "'" +
            "}";
    }

    public RegularExpression getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(RegularExpression regularExpression) {
        this.regularExpression = regularExpression;
    }
}
