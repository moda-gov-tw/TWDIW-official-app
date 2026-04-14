package gov.moda.dw.manager.domain.outside;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;

/**
 * A Org.
 */
@Entity
@Table(name = "org",schema = "vc_manager")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VcManagerOrg implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "org_id", length = 255)
    private String orgId;

    @NotNull
    @Size(max = 100)
    @Column(name = "org_tw_name", length = 100, nullable = false)
    private String orgTwName;

    @NotNull
    @Size(max = 100)
    @Column(name = "org_en_name", length = 100)
    private String orgEnName;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "create_time", nullable = false)
    private Instant createTime;

    @Column(name = "update_time", nullable = false)
    private Instant updateTime;

    public VcManagerOrg() {}

    public VcManagerOrg(long id, String orgId, String orgTwName, String orgEnName, String metadata, Instant createTime, Instant updateTime) {
        this.id = id;
        this.orgId = orgId;
        this.orgTwName = orgTwName;
        this.orgEnName = orgEnName;
        this.metadata = metadata;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public @NotNull @Size(max = 30) String getOrgId() {
        return orgId;
    }

    public void setOrgId(@NotNull @Size(max = 30) String orgId) {
        this.orgId = orgId;
    }

    public @NotNull @Size(max = 100) String getOrgTwName() {
        return orgTwName;
    }

    public void setOrgTwName(@NotNull @Size(max = 100) String orgTwName) {
        this.orgTwName = orgTwName;
    }

    public @NotNull @Size(max = 100) String getOrgEnName() {
        return orgEnName;
    }

    public void setOrgEnName(@NotNull @Size(max = 100) String orgEnName) {
        this.orgEnName = orgEnName;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }
}
