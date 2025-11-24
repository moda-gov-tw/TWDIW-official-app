package gov.moda.dw.verifier.oidvp.web.rest.oidvp;

import com.nimbusds.jose.jwk.JWKSet;
import gov.moda.dw.verifier.oidvp.annotation.LogAPI;
import gov.moda.dw.verifier.oidvp.annotation.LogInfo;
import gov.moda.dw.verifier.oidvp.common.ApiId;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.model.MetadataFieldsRequest;
import gov.moda.dw.verifier.oidvp.model.Response;
import gov.moda.dw.verifier.oidvp.service.metadata.MetadataService;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/oidvp")
public class WellKnownController {

    @Autowired MetadataService metadataService;

    /**
     * get client metadata of verifier
     *
     * @return verifier metadata
     */
    @LogAPI(ApiId.VP005)
    @GetMapping(path = ".well-known/openid-configuration", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getVerifierMetadata() throws SQLException {
        String metadataJson = metadataService.getVerifierMetadata().toString();
        return new ResponseEntity<>(metadataJson, HttpStatus.OK);
    }

    /**
     * get the jwk set of verifier
     *
     * @return jwk set
     */
    @GetMapping(path = "jwks", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getPublicJWKSet() {
        JWKSet jwkSet = metadataService.getPublicJWKSet();
        Map<String, Object> jwks = (jwkSet == null) ? Collections.emptyMap() : jwkSet.toJSONObject();
        return new ResponseEntity<>(jwks, HttpStatus.OK);
    }

    @Deprecated
    @LogInfo
//    @PostMapping(path = "metadataProperty/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> saveMetadataProperty(@RequestBody MetadataFieldsRequest request) throws SQLException {
        metadataService.saveMetadataProperty(request);
        final OidvpError error = OidvpError.SUCCESS;
        Response response = new Response();
        response.setCode(error.getCode());
        response.setMessage(error.getMsg());
        return new ResponseEntity<>(response, error.getHttpStatus());
    }

    @Deprecated
    @LogInfo
//    @PostMapping(path = "metadataProperty/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> removeMetadataProperty(@RequestBody MetadataFieldsRequest request) throws SQLException {
        metadataService.deleteMetadataProperty(request);
        final OidvpError error = OidvpError.SUCCESS;
        Response response = new Response();
        response.setCode(error.getCode());
        response.setMessage(error.getMsg());
        return new ResponseEntity<>(response, error.getHttpStatus());
    }
}
