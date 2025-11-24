package gov.moda.dw.issuer.vc.domain;

import java.sql.Timestamp;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * entity of Org in schema vc_manager
 *
 * @version 20241206
 */
@Entity //標示為實體類別
@Table(name = "org", schema = "vc_manager") //DB_TABLE_NAME
public class OrgEntity {

	@Id //標示何者為Primary Key
	@Column(name="id")
	private int id;
	
	@Column(name="org_id")
	private String orgId;
	
	@Column(name="org_tw_name")
	private String orgTwName;
	
	@Column(name="org_en_name")
	private String orgEnName;
	
//	@Column(name="metadata")
//	private String metadata;
	
	@Column(name="create_time")
	private Timestamp createTime;
	
	@Column(name="update_time")
	private Timestamp updateTime;
	
	@Column(name="vc_data_source")
	private Integer vcDataSource;

	public int getId() {
		return id;
	}

	public OrgEntity setId(int id) {
		this.id = id;
		return this;
	}

	public String getOrgId() {
		return orgId;
	}

	public OrgEntity setOrgId(String orgId) {
		this.orgId = orgId;
		return this;
	}

	public String getOrgTwName() {
		return orgTwName;
	}

	public OrgEntity setOrgTwName(String orgTwName) {
		this.orgTwName = orgTwName;
		return this;
	}

	public String getOrgEnName() {
		return orgEnName;
	}

	public OrgEntity setOrgEnName(String orgEnName) {
		this.orgEnName = orgEnName;
		return this;
	}

//	public String getMetadata() {
//		return metadata;
//	}
//
//	public OrgEntity setMetadata(String metadata) {
//		this.metadata = metadata;
//		return this;
//	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public OrgEntity setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
		return this;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public OrgEntity setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
		return this;
	}
	
	public Integer getVcDataSource() {
		return vcDataSource;
	}

	public OrgEntity setVcDataSource(Integer vcDataSource) {
		this.vcDataSource = vcDataSource;
		return this;
	}

	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
