package gov.moda.dw.issuer.oidvci.web.rest.errors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;

@ControllerAdvice
public class OIDVCIExceptionHandler {

	@ExceptionHandler(HTTPConnectException.class)
    public ResponseEntity<?> handleHTTPConnectException(HTTPConnectException ex) {
        JSONObject retJson = new JSONObject();
        retJson.put("resp_code", ex.getError_code());
        retJson.put("resp_message", ex.getError_message());
        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body(retJson.toJSONString(JSONStyle.LT_COMPRESS));
    }
}
