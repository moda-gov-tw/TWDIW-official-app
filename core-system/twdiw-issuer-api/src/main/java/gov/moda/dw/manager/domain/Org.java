package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Org.
 */
@Entity
@Table(name = "org")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Org implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 組織代碼
     */
    @NotNull
    @Size(max = 255)
    @Column(name = "org_id", length = 255, nullable = false)
    private String orgId;

    /**
     * 組織中文名稱
     */
    @NotNull
    @Size(max = 100)
    @Column(name = "org_tw_name", length = 100, nullable = false)
    private String orgTwName;

    /**
     * 組織英文名稱
     */
    @NotNull
    @Size(max = 100)
    @Column(name = "org_en_name", length = 100, nullable = false)
    private String orgEnName;

    /**
     * 資料
     */
    //@Lob
    @Column(name = "metadata")
    private String metadata;

    /**
     * 建立日期
     */
    @Column(name = "create_time")
    private Instant createTime;

    /**
     * 更新日期
     */
    @Column(name = "update_time")
    private Instant updateTime;

    @Column(name = "vc_data_source")
    private Long vcDataSource;

    /**
     * 組織 Logo (正方形 base64)
     */
    //@Lob
    @Column(name = "logo_square")
    private String logoSquare;

    /**
     * 組織 Logo (長方形 base64)
     */
    //@Lob
    @Column(name = "logo_rectangle")
    private String logoRectangle;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Org id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgId() {
        return this.orgId;
    }

    public Org orgId(String orgId) {
        this.setOrgId(orgId);
        return this;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgTwName() {
        return this.orgTwName;
    }

    public Org orgTwName(String orgTwName) {
        this.setOrgTwName(orgTwName);
        return this;
    }

    public void setOrgTwName(String orgTwName) {
        this.orgTwName = orgTwName;
    }

    public String getOrgEnName() {
        return this.orgEnName;
    }

    public Org orgEnName(String orgEnName) {
        this.setOrgEnName(orgEnName);
        return this;
    }

    public void setOrgEnName(String orgEnName) {
        this.orgEnName = orgEnName;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public Org metadata(String metadata) {
        this.setMetadata(metadata);
        return this;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Instant getCreateTime() {
        return this.createTime;
    }

    public Org createTime(Instant createTime) {
        this.setCreateTime(createTime);
        return this;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Instant getUpdateTime() {
        return this.updateTime;
    }

    public Org updateTime(Instant updateTime) {
        this.setUpdateTime(updateTime);
        return this;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    public Long getVcDataSource() {
        return this.vcDataSource;
    }

    public Org vcDataSource(Long vcDataSource) {
        this.setVcDataSource(vcDataSource);
        return this;
    }

    public void setVcDataSource(Long vcDataSource) {
        this.vcDataSource = vcDataSource;
    }

    public String getLogoSquare() {
        return this.logoSquare;
    }

    public Org logoSquare(String logoSquare) {
        this.setLogoSquare(logoSquare);
        return this;
    }

    public void setLogoSquare(String logoSquare) {
        this.logoSquare = logoSquare;
    }

    public String getLogoRectangle() {
        return this.logoRectangle;
    }

    public Org logoRectangle(String logoRectangle) {
        this.setLogoRectangle(logoRectangle);
        return this;
    }

    public void setLogoRectangle(String logoRectangle) {
        this.logoRectangle = logoRectangle;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Org)) {
            return false;
        }
        return getId() != null && getId().equals(((Org) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Org{" +
            "id=" + getId() +
            ", orgId='" + getOrgId() + "'" +
            ", orgTwName='" + getOrgTwName() + "'" +
            ", orgEnName='" + getOrgEnName() + "'" +
            ", metadata='" + getMetadata() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", updateTime='" + getUpdateTime() + "'" +
            ", vcDataSource=" + getVcDataSource() +
            ", logoSquare='" + getLogoSquare() + "'" +
            ", logoRectangle='" + getLogoRectangle() + "'" +
            "}";
    }
}
