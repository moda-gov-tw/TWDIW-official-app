package gov.moda.dw.manager.domain.outside;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * VCItemField VC欄位
 */
//@Entity
//@Table(name = "vc_item_field",schema = "vc_manager")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VCItemField implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * VC id 外鍵
     */
    @NotNull
    @Column(name = "vc_item_id", nullable = false)
    private Long vcItemId;

    /**
     * 欄位類別(BASIC NORMAL NORMAL)
     */
    @NotNull
    @Size(max = 10)
    @Column(name = "type", length = 10, nullable = false)
    private String type;

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


    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VCItemField id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVcItemId() {
        return this.vcItemId;
    }

    public VCItemField vcItemId(Long vcItemId) {
        this.setVcItemId(vcItemId);
        return this;
    }

    public void setVcItemId(Long vcItemId) {
        this.vcItemId = vcItemId;
    }

    public String getType() {
        return this.type;
    }

    public VCItemField type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCname() {
        return this.cname;
    }

    public VCItemField cname(String cname) {
        this.setCname(cname);
        return this;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getEname() {
        return this.ename;
    }

    public VCItemField ename(String ename) {
        this.setEname(ename);
        return this;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VCItemField)) {
            return false;
        }
        return getId() != null && getId().equals(((VCItemField) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VCItemField{" +
            "id=" + getId() +
            ", vcItemId=" + getVcItemId() +
            ", type='" + getType() + "'" +
            ", cname='" + getCname() + "'" +
            ", ename='" + getEname() + "'" +
            "}";
    }
}
