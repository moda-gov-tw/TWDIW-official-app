package gov.moda.dw.issuer.vc.web.rest;

import gov.moda.dw.issuer.vc.service.StatusListService;
import gov.moda.dw.issuer.vc.util.Console;
import gov.moda.dw.issuer.vc.util.Tuple;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * status list controller
 *
 * @version 20240902
 */
@RestController
@RequestMapping("/api")
public class StatusListResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusListResource.class);

    private final StatusListService statusListService;

    public StatusListResource(StatusListService statusListService) {
        this.statusListService = statusListService;
    }

    /**
     * generate and renew status list
     *
     * @param credentialType associated credential type, ex: CitizenCredential
     *
     * @return status list
     */
    @PostMapping(path = "/status-list/{credentialType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> generateAndRenew(@PathVariable String credentialType, 
    											   @RequestParam(defaultValue = "revocation") String statusListType,
    											   @RequestParam(defaultValue = "0") String gid) {

        String cleanGid = Jsoup.clean(gid, Safelist.none());

        LOGGER.info("\n[status list generate request]::\ncredential type = {}\n statusListType = {}\n gid = {}\n cleanGid = {}",
        		credentialType, statusListType, gid, Integer.parseInt(cleanGid));

        Tuple.Pair<String, HttpStatus> result = statusListService.generateAndRenew(credentialType, statusListType, Integer.parseInt(cleanGid));

        LOGGER.info("[HTTP status]:: {}", result.getB().toString());
        LOGGER.info("[query result]:: {}\n", result.getA());
        Console.showJson(result.getA(), "status list generate response");

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
     * generate status_list signing key
     *
     * @return public key
     */
    @PostMapping(path = "/status-list/genKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> genKey(){
    
    	LOGGER.info("\n[Generate status_list signing key]::\n");
    	
    	Tuple.Pair<String, HttpStatus> result = statusListService.genKey();
    	
    	LOGGER.info("[HTTP status]:: {}", result.getB().toString());
        LOGGER.info("[register result]:: {}\n", result.getA());
        Console.showJson(result.getA(), "Generate status_list signing key response");
        
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
