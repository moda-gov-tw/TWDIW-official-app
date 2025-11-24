package gov.moda.dw.issuer.vc.web.rest;

import gov.moda.dw.issuer.vc.service.CredentialService;
import gov.moda.dw.issuer.vc.service.dto.CredentialRequestDTO;
import gov.moda.dw.issuer.vc.service.dto.CredentialTransferRequestDTO;
import gov.moda.dw.issuer.vc.util.Tuple;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * credential controller
 *
 * @version 20240902
 */
@RestController
@RequestMapping("/api")
public class CredentialResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialResource.class);

    private final CredentialService credentialService;

    public CredentialResource(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    /**
     * generate credential
     *
     * @param credentialRequest credential generate request
     * @return credential
     */
    @PostMapping(path = "/credential", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasAuthority('dwissuervc201i')")
    public ResponseEntity<String> generate(@RequestBody CredentialRequestDTO credentialRequest) {

        LOGGER.info("[credential generate request] received");

        Tuple.Pair<String, HttpStatus> result = credentialService.generate(credentialRequest);

        LOGGER.info("[credential generate result] HTTP status = {}", result.getB());

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

    /**
     * update credential status
     *
     * @param cid credential identifier, ex: 99928d83-f1c1-4eb8-935a-9a165e15d654
     * @param action operation action, ex: revocation, suspension, recovery
     * @return current credential status
     */
    @PutMapping(path = "/credential/{cid}/{action}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateStatus(@PathVariable String cid, @PathVariable String action) {

        String cleanCid = Jsoup.clean(cid, Safelist.none());

        LOGGER.info("[credential status update request] received action = {}", action);

        // TODO: handle other action
        if ("revocation".equalsIgnoreCase(action)) {

            Tuple.Pair<String, HttpStatus> result = credentialService.revoke(cleanCid);

            LOGGER.info("[credential status update result] HTTP status = {}", result.getB());

            // with response content when http status = 200/400/404/500
            if (result.getB() == HttpStatus.OK ||
                result.getB() == HttpStatus.BAD_REQUEST ||
                result.getB() == HttpStatus.NOT_FOUND ||
                result.getB() == HttpStatus.INTERNAL_SERVER_ERROR) {
                return new ResponseEntity<>(result.getA(), result.getB());
            } else {
                return new ResponseEntity<>(result.getB());
            }

        } else if ("suspension".equalsIgnoreCase(action)) {
        	
        	Tuple.Pair<String, HttpStatus> result = credentialService.suspend(cleanCid);
        	
        	LOGGER.info("[credential status update result] HTTP status = {}", result.getB());

            // with response content when http status = 200/400/404/500
            if (result.getB() == HttpStatus.OK ||
                result.getB() == HttpStatus.BAD_REQUEST ||
                result.getB() == HttpStatus.NOT_FOUND ||
                result.getB() == HttpStatus.INTERNAL_SERVER_ERROR) {
                return new ResponseEntity<>(result.getA(), result.getB());
            } else {
                return new ResponseEntity<>(result.getB());
            }
        	
        } else if ("recovery".equalsIgnoreCase(action)) {
        	
        	Tuple.Pair<String, HttpStatus> result = credentialService.recover(cleanCid);
        	
        	LOGGER.info("[credential status update result] HTTP status = {}", result.getB());

            // with response content when http status = 200/400/404/500
            if (result.getB() == HttpStatus.OK ||
                result.getB() == HttpStatus.BAD_REQUEST ||
                result.getB() == HttpStatus.NOT_FOUND ||
                result.getB() == HttpStatus.INTERNAL_SERVER_ERROR) {
                return new ResponseEntity<>(result.getA(), result.getB());
            } else {
                return new ResponseEntity<>(result.getB());
            }
        	
        } else  {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * query credential
     *
     * @param cid credential identifier, ex: 99928d83-f1c1-4eb8-935a-9a165e15d654
     * @return credential
     */
    @GetMapping(path = "/credential/{cid}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasAuthority('dwissuervc403i')")
    public ResponseEntity<String> query(@PathVariable String cid) {

        LOGGER.info("[credential query request] received");

        Tuple.Pair<String, HttpStatus> result = credentialService.query(cid);

        LOGGER.info("[credential query result] HTTP status = {}", result.getB());

        // with response content when http status = 200/400/404/500
        if (result.getB() == HttpStatus.OK ||
            result.getB() == HttpStatus.BAD_REQUEST ||
            result.getB() == HttpStatus.NOT_FOUND ||
            result.getB() == HttpStatus.INTERNAL_SERVER_ERROR) {
            return new ResponseEntity<>(result.getA(), result.getB());
        } else {
            return new ResponseEntity<>(result.getB());
        }
    }

    /**
     * query credential by nonce
     *
     * @param nonce nonce value, ex: 3531dcd0-8e21-44c5-8908-98f3b6fd5e9f
     * @return credential
     */
    @GetMapping(path = "/credential/nonce/{nonce}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasAuthority('dwissuervc402i')")
    public ResponseEntity<String> queryByNonce(@PathVariable String nonce) {

        LOGGER.info("[credential query by nonce request] received");

        Tuple.Pair<String, HttpStatus> result = credentialService.queryByNonce(nonce);

        LOGGER.info("[credential query by nonce result] HTTP status = {}", result.getB());

        // with response content when http status = 200/400/404/500
        if (result.getB() == HttpStatus.OK ||
            result.getB() == HttpStatus.BAD_REQUEST ||
            result.getB() == HttpStatus.NOT_FOUND ||
            result.getB() == HttpStatus.INTERNAL_SERVER_ERROR) {
            return new ResponseEntity<>(result.getA(), result.getB());
        } else {
            return new ResponseEntity<>(result.getB());
        }
    }

    /**
     * query all credentials
     *
     * @return all credentials
     */

    @GetMapping(path = "/credentials/{credentialType}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasAuthority('dwissuervc401i')")
    public ResponseEntity<String> queryAll(@PathVariable String credentialType,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(defaultValue = "ASC") String direction) {

        LOGGER.info("[credential query all request] received");

        Tuple.Pair<String, HttpStatus> result = credentialService.queryAll(credentialType, page, size, direction);

        LOGGER.info("[credential query all result] HTTP status = {}", result.getB());

        // with response content when http status = 200/400/404/500
        if (result.getB() == HttpStatus.OK ||
            result.getB() == HttpStatus.BAD_REQUEST ||
            result.getB() == HttpStatus.NOT_FOUND ||
            result.getB() == HttpStatus.INTERNAL_SERVER_ERROR) {
            return new ResponseEntity<>(result.getA(), result.getB());
        } else {
            return new ResponseEntity<>(result.getB());
        }
    }
    
    /**
     * transfer credential
     *
     * @param VP
     * @return QRCode
     */
    @PostMapping(path = "/credential/{cid}/transfer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> transfer(@PathVariable String cid, @RequestBody CredentialTransferRequestDTO credentialTransferRequest) {

        LOGGER.info("[credential transfer request] received");

        Tuple.Pair<String, HttpStatus> result = credentialService.transfer(cid, credentialTransferRequest);

        LOGGER.info("[credential transfer result] HTTP status = {}", result.getB());

        // with response content when http status = 200/400/404/500
        if (result.getB() == HttpStatus.OK ||
            result.getB() == HttpStatus.BAD_REQUEST ||
            result.getB() == HttpStatus.NOT_FOUND ||
            result.getB() == HttpStatus.INTERNAL_SERVER_ERROR) {
            return new ResponseEntity<>(result.getA(), result.getB());
        } else {
            return new ResponseEntity<>(result.getB());
        }
    }
}
