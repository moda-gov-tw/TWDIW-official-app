package gov.moda.dw.issuer.vc.service.dto;

import java.io.Serial;
import java.io.Serializable;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import gov.moda.dw.issuer.vc.vo.VcStatus;

public class OperateStatusListRequestDTO implements Serializable {
	
	@Serial
    private static final long serialVersionUID = 1L;
	
	private String opType;
	
	private String credentialType;
	
	private String statusListType;
	
	private int gid;
	
	private String issuerDid;
	
	private int statusListIndex;
	
	private VcStatus vcStatus;
	
	public OperateStatusListRequestDTO() {
		
	}
	
	public OperateStatusListRequestDTO(String opType,
									   String credentialType,
									   String statusListType,
									   int gid,
									   String issuerDid,
									   int statusListIndex,
									   VcStatus vcStatus) {
		this.opType = opType;
		this.credentialType = credentialType;
		this.statusListType = statusListType;
		this.gid = gid;
		this.issuerDid = issuerDid;
		this.statusListIndex = statusListIndex;
		this.vcStatus = vcStatus;
	}
	
	public OperateStatusListRequestDTO(String reqJson) {
		if(reqJson != null && !reqJson.isBlank()) {
			OperateStatusListRequestDTO operateStatusListRequest = JsonUtils.jsToVo(reqJson, this.getClass());
			if(operateStatusListRequest != null) {
				this.opType = operateStatusListRequest.getOpType();
				this.credentialType = operateStatusListRequest.getCredentialType();
				this.statusListType = operateStatusListRequest.getStatusListType();
				this.gid = operateStatusListRequest.getGid();
				this.issuerDid = operateStatusListRequest.getIssuerDid();
				this.statusListIndex = operateStatusListRequest.getStatusListIndex();
				this.vcStatus = operateStatusListRequest.getVcStatus();
			}
		}
	}

	public String getOpType() {
		return opType;
	}

	public OperateStatusListRequestDTO setOpType(String opType) {
		this.opType = opType;
		return this;
	}

	public String getCredentialType() {
		return credentialType;
	}

	public OperateStatusListRequestDTO setCredentialType(String credentialType) {
		this.credentialType = credentialType;
		return this;
	}

	public String getStatusListType() {
		return statusListType;
	}

	public OperateStatusListRequestDTO setStatusListType(String statusListType) {
		this.statusListType = statusListType;
		return this;
	}

	public int getGid() {
		return gid;
	}

	public OperateStatusListRequestDTO setGid(int gid) {
		this.gid = gid;
		return this;
	}

	public String getIssuerDid() {
		return issuerDid;
	}

	public OperateStatusListRequestDTO setIssuerDid(String issuerDid) {
		this.issuerDid = issuerDid;
		return this;
	}

	public int getStatusListIndex() {
		return statusListIndex;
	}

	public OperateStatusListRequestDTO setStatusListIndex(int statusListIndex) {
		this.statusListIndex = statusListIndex;
		return this;
	}

	public VcStatus getVcStatus() {
		return vcStatus;
	}

	public OperateStatusListRequestDTO setVcStatus(VcStatus vcStatus) {
		this.vcStatus = vcStatus;
		return this;
	}
	
	public boolean validate() {
		
		return opType != null && !opType.isBlank() &&
				credentialType != null && !credentialType.isBlank() &&
				statusListType != null && !statusListType.isBlank() &&
				gid >= 0 &&
				issuerDid != null && !issuerDid.isBlank() &&
				statusListIndex >= -1;
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
