package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.Invoice} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InvoiceDTO implements Serializable {

    private Long id;

    @Size(max = 10)
    @Schema(description = "綠界發票號碼")
    private String ecpayInvoiceNo;

    @Size(max = 30)
    @Schema(description = "綠界特店自訂編號")
    private String ecpayInvoiceRelateNumber;

    @Size(max = 255)
    @Schema(description = "訂單編號")
    private String orderCaseId;

    @Size(max = 255)
    @Schema(description = "月結訂單編號")
    private String monthlyOrderCaseId;

    @Size(max = 255)
    @Schema(description = "發票類型")
    private String invoiceType;

    @Schema(description = "發票金額")
    private Long amount;

    @Size(max = 255)
    @Schema(description = "狀態")
    private String status;

    @Schema(description = "是否正在人工處理中")
    private Boolean isUnderManualProcessing;

    @Size(max = 255)
    @Schema(description = "訂單類型")
    private String orderType;

    @Size(max = 255)
    @Schema(description = "付款方式")
    private String paymentType;

    @Size(max = 255)
    @Schema(description = "發票開立種類")
    private String issueType;

    @Size(max = 50)
    @Schema(description = "綠界發票開立時間")
    private String ecpayInvoiceDate;

    @Schema(description = "創建時間")
    private Instant createTime;

    @Size(max = 8)
    @Schema(description = "統一編號")
    private String taxIdNumber;

    @Size(max = 60)
    @Schema(description = "聯絡人")
    private String contact;

    @Size(max = 80)
    @Schema(description = "聯絡信箱")
    private String contactEmail;

    @Size(max = 20)
    @Schema(description = "連絡電話")
    private String contactPhone;

    @Size(max = 60)
    @Schema(description = "公司名稱")
    private String companyName;

    @Size(max = 100)
    @Schema(description = "公司地址")
    private String companyAddr;

    @Schema(description = "綠界回應代碼")
    private Integer ecpayRtnCode;

    @Size(max = 255)
    @Schema(description = "綠界回應訊息")
    private String ecpayRtnMsg;

    @Size(max = 20)
    @Schema(description = "綠界隨機碼")
    private String ecpayRandomNumber;

    @Size(max = 20)
    @Schema(description = "綠界發票作廢原因")
    private String ecpayInvoiceCancelReason;

    @Schema(description = "最後編輯時間")
    private Instant modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEcpayInvoiceNo() {
        return ecpayInvoiceNo;
    }

    public void setEcpayInvoiceNo(String ecpayInvoiceNo) {
        this.ecpayInvoiceNo = ecpayInvoiceNo;
    }

    public String getEcpayInvoiceRelateNumber() {
        return ecpayInvoiceRelateNumber;
    }

    public void setEcpayInvoiceRelateNumber(String ecpayInvoiceRelateNumber) {
        this.ecpayInvoiceRelateNumber = ecpayInvoiceRelateNumber;
    }

    public String getOrderCaseId() {
        return orderCaseId;
    }

    public void setOrderCaseId(String orderCaseId) {
        this.orderCaseId = orderCaseId;
    }

    public String getMonthlyOrderCaseId() {
        return monthlyOrderCaseId;
    }

    public void setMonthlyOrderCaseId(String monthlyOrderCaseId) {
        this.monthlyOrderCaseId = monthlyOrderCaseId;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsUnderManualProcessing() {
        return isUnderManualProcessing;
    }

    public void setIsUnderManualProcessing(Boolean isUnderManualProcessing) {
        this.isUnderManualProcessing = isUnderManualProcessing;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getEcpayInvoiceDate() {
        return ecpayInvoiceDate;
    }

    public void setEcpayInvoiceDate(String ecpayInvoiceDate) {
        this.ecpayInvoiceDate = ecpayInvoiceDate;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public String getTaxIdNumber() {
        return taxIdNumber;
    }

    public void setTaxIdNumber(String taxIdNumber) {
        this.taxIdNumber = taxIdNumber;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddr() {
        return companyAddr;
    }

    public void setCompanyAddr(String companyAddr) {
        this.companyAddr = companyAddr;
    }

    public Integer getEcpayRtnCode() {
        return ecpayRtnCode;
    }

    public void setEcpayRtnCode(Integer ecpayRtnCode) {
        this.ecpayRtnCode = ecpayRtnCode;
    }

    public String getEcpayRtnMsg() {
        return ecpayRtnMsg;
    }

    public void setEcpayRtnMsg(String ecpayRtnMsg) {
        this.ecpayRtnMsg = ecpayRtnMsg;
    }

    public String getEcpayRandomNumber() {
        return ecpayRandomNumber;
    }

    public void setEcpayRandomNumber(String ecpayRandomNumber) {
        this.ecpayRandomNumber = ecpayRandomNumber;
    }

    public String getEcpayInvoiceCancelReason() {
        return ecpayInvoiceCancelReason;
    }

    public void setEcpayInvoiceCancelReason(String ecpayInvoiceCancelReason) {
        this.ecpayInvoiceCancelReason = ecpayInvoiceCancelReason;
    }

    public Instant getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Instant modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvoiceDTO)) {
            return false;
        }

        InvoiceDTO invoiceDTO = (InvoiceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, invoiceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InvoiceDTO{" +
            "id=" + getId() +
            ", ecpayInvoiceNo='" + getEcpayInvoiceNo() + "'" +
            ", ecpayInvoiceRelateNumber='" + getEcpayInvoiceRelateNumber() + "'" +
            ", orderCaseId='" + getOrderCaseId() + "'" +
            ", monthlyOrderCaseId='" + getMonthlyOrderCaseId() + "'" +
            ", invoiceType='" + getInvoiceType() + "'" +
            ", amount=" + getAmount() +
            ", status='" + getStatus() + "'" +
            ", isUnderManualProcessing='" + getIsUnderManualProcessing() + "'" +
            ", orderType='" + getOrderType() + "'" +
            ", paymentType='" + getPaymentType() + "'" +
            ", issueType='" + getIssueType() + "'" +
            ", ecpayInvoiceDate='" + getEcpayInvoiceDate() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", taxIdNumber='" + getTaxIdNumber() + "'" +
            ", contact='" + getContact() + "'" +
            ", contactEmail='" + getContactEmail() + "'" +
            ", contactPhone='" + getContactPhone() + "'" +
            ", companyName='" + getCompanyName() + "'" +
            ", companyAddr='" + getCompanyAddr() + "'" +
            ", ecpayRtnCode=" + getEcpayRtnCode() +
            ", ecpayRtnMsg='" + getEcpayRtnMsg() + "'" +
            ", ecpayRandomNumber='" + getEcpayRandomNumber() + "'" +
            ", ecpayInvoiceCancelReason='" + getEcpayInvoiceCancelReason() + "'" +
            ", modifyTime='" + getModifyTime() + "'" +
            "}";
    }
}
