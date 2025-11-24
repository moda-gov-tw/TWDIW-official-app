package gov.moda.dw.verifier.oidvp.model.oid4vp;

import java.util.Date;

public class RequestObjectOpts {

    private String issuer;
    private Date expiredTime;

    public RequestObjectOpts() {}

    @Deprecated
    public RequestObjectOpts(String issuer) {
        if (issuer == null || issuer.isEmpty()) {
            throw new IllegalArgumentException("issuer must not be empty.");
        }
        this.issuer = issuer;
    }

    public RequestObjectOpts setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
        return this;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public String getIssuer() {
        return issuer;
    }
}
