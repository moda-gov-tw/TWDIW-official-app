package gov.moda.dw.manager.service.dto;

import java.util.List;

public class Dwvc901ResDto {

    private String transactionId;

    private String expiredDate;

    private List<Dwvc901FieldDto> fields;


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public List<Dwvc901FieldDto> getFields() {
        return fields;
    }

    public void setFields(List<Dwvc901FieldDto> fields) {
        this.fields = fields;
    }
}
