package gov.moda.dw.manager.service.dto;

import java.util.LinkedHashMap;
import java.util.List;

public class VcItemData305iResponseDTO {

    private String code;
    private String message;
    private String credential_type;
    private String nonce;
    private String transactionId;
    private String expiredDate;
    private LinkedHashMap<String, Object> data;
    private List<FieldItem> fields;
    private String sandbox_code;
    private String sandbox_message;
    private String sandbox_type;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCredential_type() {
        return credential_type;
    }

    public void setCredential_type(String credential_type) {
        this.credential_type = credential_type;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public LinkedHashMap<String, Object> getData() {
        return data;
    }

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

    public void setData(LinkedHashMap<String, Object> data) {
        this.data = data;
    }

    public String getSandbox_code() {
        return sandbox_code;
    }

    public void setSandbox_code(String sandbox_code) {
        this.sandbox_code = sandbox_code;
    }

    public String getSandbox_message() {
        return sandbox_message;
    }

    public void setSandbox_message(String sandbox_message) {
        this.sandbox_message = sandbox_message;
    }

    public String getSandbox_type() {
        return sandbox_type;
    }

    public void setSandbox_type(String sandbox_type) {
        this.sandbox_type = sandbox_type;
    }

    public List<FieldItem> getFields() {
        return fields;
    }

    public void setFields(List<FieldItem> fields) {
        this.fields = fields;
        convertFieldsToDataMap();
    }

    public static class FieldItem {
        private String ename;
        private String content;

        public String getEname() {
            return ename;
        }

        public void setEname(String ename) {
            this.ename = ename;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    private void convertFieldsToDataMap() {

        LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
        for (FieldItem item : fields) {
            dataMap.put(item.getEname(), item.getContent());
        }

        data = dataMap;
    }
}
