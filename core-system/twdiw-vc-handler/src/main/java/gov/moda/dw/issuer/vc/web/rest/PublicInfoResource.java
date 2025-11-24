package gov.moda.dw.issuer.vc.web.rest;

import gov.moda.dw.issuer.vc.service.PublicInfoService;
import gov.moda.dw.issuer.vc.service.dto.StatusListResponseDTO;
import gov.moda.dw.issuer.vc.util.Console;
import gov.moda.dw.issuer.vc.util.Tuple;
import gov.moda.dw.issuer.vc.vo.Definition;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.jsoup.Jsoup;

/**
 * public information controller
 *
 * @version 20240902
 */
@RestController
@RequestMapping("/api")
public class PublicInfoResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicInfoResource.class);

    private final PublicInfoService publicInfoService;

    public PublicInfoResource(PublicInfoService publicInfoService) {
        this.publicInfoService = publicInfoService;
    }

    /**
     * get current version
     *
     * @return current version
     */
    @GetMapping(path = "/version", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> version() {
        // return current version
        return new ResponseEntity<>(Definition.CURRENT_VERSION, HttpStatus.OK);
    }

    /**
     * get status list
     *
     * @param credentialType associated credential type, ex: VirtualCardCredential
     * @param groupName associated group name, ex: r123
     * @return status list
     */
    @GetMapping(path = "/status-list/{credentialType}/{groupName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getStatusList(@PathVariable String credentialType, @PathVariable String groupName) {

        String cleanGroupName = Jsoup.clean(groupName, Safelist.none());

        LOGGER.info("\n[status list query request]::\ncredential type = {}\ngroup name = {}\nclean group name = {}", credentialType, groupName, cleanGroupName);

        Tuple.Pair<String, HttpStatus> result = publicInfoService.getStatusList(credentialType, cleanGroupName);

        LOGGER.info("[HTTP status]:: {}", result.getB().toString());
        LOGGER.info("[status list result]:: {}\n", result.getA());
        Console.showJson(result.getA(), "status list query response");

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
     * get schema
     *
     * @param schemaName schema name, ex: virtual-card-v1.json
     * @return schema
     */
    @GetMapping(path = "/schema/{schemaName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getSchema(@PathVariable String schemaName) {

        LOGGER.info("\n[schema query request]::\nschema name = {}\n", schemaName);

        Tuple.Pair<String, HttpStatus> result = publicInfoService.getSchema(schemaName);

        LOGGER.info("[HTTP status]:: {}", result.getB().toString());
        LOGGER.info("[schema result]:: {}\n", result.getA());
        Console.showJson(result.getA(), "schema query response");

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
     * get public keys
     *
     * @return public keys
     */
    @GetMapping(path = "/keys", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPublicKeys() {

        LOGGER.info("\n[public key query request]::\n");

        Tuple.Pair<String, HttpStatus> result = publicInfoService.getPublicKeys();

        LOGGER.info("[HTTP status]:: {}", result.getB().toString());
        LOGGER.info("[public key result]:: {}\n", result.getA());
        Console.showJson(result.getA(), "public key query response");

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
