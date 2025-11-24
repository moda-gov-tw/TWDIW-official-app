package gov.moda.dw.verifier.oidvp.model;

public class VerifierRegisterDidResponse {

    private String did;

    public VerifierRegisterDidResponse(String did) {
        this.did = did;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        if (did != null) {
            sb.append("\"did\":\"")
              .append(did).append('\"');
        }
        sb.append("}");
        String s = sb.toString();
        return (s.startsWith("{,")) ? s.replace("{,", "{") : s;
    }
}
