package gov.moda.dw.issuer.vc.web.rest;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.issuer.vc.service.CredentialDataService;
import gov.moda.dw.issuer.vc.service.dto.CredentialDataRequestDTO;
import gov.moda.dw.issuer.vc.util.Console;
import gov.moda.dw.issuer.vc.util.Tuple;

/**
 * credential data controller
 *
 * @version 20241017
 */
@RestController
@RequestMapping("/api")
public class CredentialDataResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(CredentialDataResource.class);

	private final CredentialDataService credentialDataService;

	public CredentialDataResource(CredentialDataService credentialDataService) {
		this.credentialDataService = credentialDataService;
	}

	/**
     * set credential data
     *
     * @param set credential data request
     * @return execute result
     */
	@PostMapping(path = "/setdata", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasAuthority('dwissuer-vc-501i	')")
    public ResponseEntity<String> putDataToDB(@RequestBody CredentialDataRequestDTO credentialDataRequest) {

		String transactionId = null;
		String credentialType = null;
		Map<String, Object> data = null;

		if (credentialDataRequest != null && credentialDataRequest.validate()) {
			transactionId = credentialDataRequest.getTransactionId();
			credentialType = credentialDataRequest.getCredentialType();
            data = credentialDataRequest.getData();
        }

		LOGGER.info("[setDataToDB]::transactionId = {}, credentialType = {}, data = {}", transactionId, credentialType, data);

		Tuple.Pair<String, HttpStatus> result = credentialDataService.putDataToDB(credentialDataRequest);
		LOGGER.info("[HTTP status]:: {}", result.getB().toString());
        LOGGER.info("[putData result]:: {}\n", result.getA());
        Console.showJson(result.getA(), "put credential data response");

        // with response content when http status = 201/400/404/500
        if (result.getB() == HttpStatus.CREATED ||
            result.getB() == HttpStatus.BAD_REQUEST ||
            result.getB() == HttpStatus.NOT_FOUND ||
            result.getB() == HttpStatus.INTERNAL_SERVER_ERROR) {
            return new ResponseEntity<>(result.getA(), result.getB());
        } else {
            return new ResponseEntity<>(result.getB());
        }
	}
}
