package gov.moda.dw.verifier.oidvp.service.oidvp.customData;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;

public class CustomData {

    private JsonNode data = null;
    String dataValue;

    public CustomData(JsonNode data) {
        this.data = data;
    }

    public CustomData(String dataValue) {
        this.dataValue = dataValue;
    }

    @JsonValue
    @JsonRawValue
    public String getJsonValue() {
        if (data != null) {
            return data.isNull() ? null : data.toString();
        } else {
            return dataValue == null || dataValue.isEmpty() ? null : dataValue;
        }
    }
}
