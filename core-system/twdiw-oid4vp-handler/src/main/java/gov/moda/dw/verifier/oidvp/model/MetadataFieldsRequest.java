package gov.moda.dw.verifier.oidvp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MetadataFieldsRequest {

    @JsonProperty("client_name")
    private String clientName;

    @JsonProperty("tos_uri")
    private String tosURI;

    @JsonProperty("remove_fields")
    private List<String> removeFields;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getTosURI() {
        return tosURI;
    }

    public void setTosURI(String tosURI) {
        this.tosURI = tosURI;
    }

    public List<String> getRemoveFields() {
        return removeFields;
    }

    public void setRemoveFields(List<String> removeFields) {
        this.removeFields = removeFields;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        if (clientName != null) {
            sb.append("\"clientName\":\"")
              .append(clientName).append('\"');
        }
        if (tosURI != null) {
            sb.append(",\"tosURI\":\"")
              .append(tosURI).append('\"');
        }
        if (removeFields != null) {
            sb.append(",\"removeFields\":")
              .append(removeFields);
        }
        sb.append("}");
        String s = sb.toString();
        return (s.startsWith("{,")) ? s.replace("{,", "{") : s;
    }
}
