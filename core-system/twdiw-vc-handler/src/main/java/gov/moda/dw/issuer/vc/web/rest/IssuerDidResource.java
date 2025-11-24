package gov.moda.dw.issuer.vc.web.rest;

import gov.moda.dw.issuer.vc.service.IssuerDidService;
import gov.moda.dw.issuer.vc.service.dto.IssuerDidRequestDTO;
import gov.moda.dw.issuer.vc.service.dto.IssuerDidResponseDTO;
import gov.moda.dw.issuer.vc.util.Console;
import gov.moda.dw.issuer.vc.util.Tuple;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Issuer DID controller
 *
 * @version 20240902
 */
@RestController
@RequestMapping("/api")
public class IssuerDidResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssuerDidResource.class);

    public static final String SAMPLE_DID = new IssuerDidResponseDTO("did:example:ebfeb1f712ebc6f1c276e12ec21").toString();

    private final IssuerDidService issuerDidService;

    public IssuerDidResource(IssuerDidService issuerDidService) {
        this.issuerDidService = issuerDidService;
    }

    /**
     * register DID
     *
     * @param issuerDidRequest DID register request
     * @return DID information
     */

    @PostMapping(path = "/did", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    //    @PreAuthorize("hasAuthority('dwissuervc101i')")
    public ResponseEntity<String> register(@RequestBody IssuerDidRequestDTO issuerDidRequest) {

        LOGGER.info("\n[DID register request]::\n");
        Map<String, Object> org = null;
        String p7data = null;
        
        if(issuerDidRequest != null && issuerDidRequest.validate()) {
        	org = issuerDidRequest.getOrg();
        	p7data = Optional.ofNullable(issuerDidRequest.getP7data())
        			.filter(p7 -> !p7.isBlank())
        			.orElse("");
        }
        
        LOGGER.info("[DID register request]::org = {}, p7data = {}", org, p7data);

        Tuple.Pair<String, HttpStatus> result = issuerDidService.register(issuerDidRequest);

        LOGGER.info("[HTTP status]:: {}", result.getB().toString());
        LOGGER.info("[register result]:: {}\n", result.getA());
        Console.showJson(result.getA(), "DID register response");

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
