package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import gov.moda.dw.manager.annotation.RequiredField;
import gov.moda.dw.manager.annotation.ToMapDTO;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.Receipt} entity.
 */
@ToMapDTO
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReceiptDTO implements Serializable {

  @RequiredField
  private Long id;

  @NotNull
  @Schema(description = "收據案件編號", requiredMode = Schema.RequiredMode.REQUIRED)
  private String caseId;

  @Schema(description = "收據流水號")
  private String uid;

  @Schema(description = "訂單編號")
  private String orderCaseId;

  @Schema(description = "收據類型")
  private String orderType;

  @Schema(description = "收據金額")
  private Long amount;

  @Schema(description = "月結訂單編號")
  private String monthlyOrderCaseId;

  @Schema(description = "顯示名稱類別")
  private String nameType;

  @Schema(description = "項目")
  private String content;

  @Schema(description = "狀態")
  private String status;

  @Schema(description = "申請人")
  private String applicant;

  @Schema(description = "下位簽核人員")
  private String nextAudit;

  @Schema(description = "最後修改日期")
  private Instant modifyTime;

  @Schema(description = "建立時間")
  private Instant createTime;

  @Schema(description = "收據電子檔案建立時間")
  private Instant pdfCreateTime;

  @Schema(description = "收據電子檔案建立時間")
  private String contact;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCaseId() {
    return caseId;
  }

  public void setCaseId(String caseId) {
    this.caseId = caseId;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getOrderCaseId() {
    return orderCaseId;
  }

  public void setOrderCaseId(String orderCaseId) {
    this.orderCaseId = orderCaseId;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public Long getAmount() {
    return amount;
  }

  public void setAmount(Long amount) {
    this.amount = amount;
  }

  public String getMonthlyOrderCaseId() {
    return monthlyOrderCaseId;
  }

  public void setMonthlyOrderCaseId(String monthlyOrderCaseId) {
    this.monthlyOrderCaseId = monthlyOrderCaseId;
  }

  public String getNameType() {
    return nameType;
  }

  public void setNameType(String nameType) {
    this.nameType = nameType;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getApplicant() {
    return applicant;
  }

  public void setApplicant(String applicant) {
    this.applicant = applicant;
  }

  public String getNextAudit() {
    return nextAudit;
  }

  public void setNextAudit(String nextAudit) {
    this.nextAudit = nextAudit;
  }

  public Instant getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(Instant modifyTime) {
    this.modifyTime = modifyTime;
  }

  public Instant getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Instant createTime) {
    this.createTime = createTime;
  }

  public Instant getPdfCreateTime() {
    return pdfCreateTime;
  }

  public void setPdfCreateTime(Instant pdfCreateTime) {
    this.pdfCreateTime = pdfCreateTime;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ReceiptDTO)) {
      return false;
    }

    ReceiptDTO receiptDTO = (ReceiptDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, receiptDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "ReceiptDTO{" +
            "id=" + getId() +
            ", caseId='" + getCaseId() + "'" +
            ", uid='" + getUid() + "'" +
            ", orderCaseId='" + getOrderCaseId() + "'" +
            ", orderType='" + getOrderType() + "'" +
            ", amount=" + getAmount() +
            ", monthlyOrderCaseId='" + getMonthlyOrderCaseId() + "'" +
            ", nameType='" + getNameType() + "'" +
            ", content='" + getContent() + "'" +
            ", status='" + getStatus() + "'" +
            ", applicant='" + getApplicant() + "'" +
            ", nextAudit='" + getNextAudit() + "'" +
            ", modifyTime='" + getModifyTime() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", pdfCreateTime='" + getPdfCreateTime() + "'" +
            ", contact='" + getContact() + "'" +
            "}";
    }
}
