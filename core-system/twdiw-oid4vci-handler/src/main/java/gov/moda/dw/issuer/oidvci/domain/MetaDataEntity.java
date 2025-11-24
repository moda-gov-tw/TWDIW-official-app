package gov.moda.dw.issuer.oidvci.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity //標示為實體類別
@Table(name = "org",schema = "vc_manager") //DB_TABLE_NAME
public class MetaDataEntity
{
    @Id //標示何者為Primary Key
    @Column(name="id")
    private Long Id;

    @Column(name="org_id")
    private String orgId;

    @Column(name="org_tw_name")
    private String orgTwName;

    @Column(name="org_en_name")
    private String orgEnName;

    @Column(name="metadata")
    private String Metadata;

    @Column(name="create_time")
    private String createTime;

    @Column(name="update_time")
    private String updateTime;


    public Long getID() {
        return Id;
    }

    public void setID(Long Id) {
        this.Id = Id;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgTwName() {
        return orgTwName;
    }

    public void setOrgTwName(String orgTwName) {
        this.orgTwName = orgTwName;
    }

    public String getOrgEnName() {
        return orgEnName;
    }

    public void setOrgEnName(String orgEnName) {
        this.orgEnName = orgEnName;
    }

    public String getMetadata() {
        return Metadata;
    }

    public void setMetadata(String Metadata) {
        this.Metadata = Metadata;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
