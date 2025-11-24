package gov.moda.dw.verifier.oidvp.web.rest.oidvp;

import gov.moda.dw.verifier.oidvp.annotation.LogAPI;
import gov.moda.dw.verifier.oidvp.annotation.LogInfo;
import gov.moda.dw.verifier.oidvp.common.ApiId;
import gov.moda.dw.verifier.oidvp.model.VerifierRegisterDidRequest;
import gov.moda.dw.verifier.oidvp.model.VerifierRegisterDidResponse;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpException;
import gov.moda.dw.verifier.oidvp.service.did.DIDService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oidvp")
@LogInfo
public class VerifierDidController {

    private final DIDService didService;

    public VerifierDidController(DIDService didService) {
        this.didService = didService;
    }

    @LogAPI(ApiId.VP009)
    @PostMapping(path = "did", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VerifierRegisterDidResponse> registerDID(@RequestBody VerifierRegisterDidRequest request) throws OidvpException {
        String did = didService.registerVerifierDID(request).did();
        VerifierRegisterDidResponse response = new VerifierRegisterDidResponse(did);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
