package gov.moda.dw.verifier.oidvp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonInclude(Include.NON_NULL)
public class PresentationDataRequest {

    @JsonProperty("business_id")
    private String businessId;

    @JsonProperty("serial_no")
    private String serialNo;

    private String mode;

    @JsonProperty("presentation_definition")
    private JsonNode presentationDefinition;


    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public JsonNode getPresentationDefinition() {
        return presentationDefinition;
    }

    public void setPresentationDefinition(JsonNode presentationDefinition) {
        this.presentationDefinition = presentationDefinition;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"business_id\":\"")
          .append(businessId).append('\"');

        sb.append(",\"serial_no\":\"")
          .append(serialNo).append('\"');

        sb.append(",\"mode\":\"")
          .append(mode).append('\"');

        sb.append(",\"presentation_definition\":")
          .append(presentationDefinition);

        sb.append('}');
        return sb.toString();
    }

    public enum Mode {
        save,
        delete;

        public static Mode getMode(String mode) {
            if (mode == null || mode.isEmpty()) {
                throw new IllegalArgumentException("unsupported mode");
            }

            for (Mode value : values()) {
                if (value.name().equals(mode)) {
                    return value;
                }
            }
            throw new IllegalArgumentException("unsupported mode");
        }
    }
}
