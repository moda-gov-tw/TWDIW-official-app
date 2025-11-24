package gov.moda.dw.issuer.vc.service.dto.frontend;

import gov.moda.dw.issuer.vc.util.JsonUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * DID review request (to frontend service)
 *
 * @version 20240902
 */
public class DidReviewReqeustDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private boolean result;

    public DidReviewReqeustDTO() {
    }

    public DidReviewReqeustDTO(String id, boolean result) {
        this.id = id;
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public DidReviewReqeustDTO setId(String id) {
        this.id = id;
        return this;
    }

    public boolean isResult() {
        return result;
    }

    public DidReviewReqeustDTO setResult(boolean result) {
        this.result = result;
        return this;
    }

	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
