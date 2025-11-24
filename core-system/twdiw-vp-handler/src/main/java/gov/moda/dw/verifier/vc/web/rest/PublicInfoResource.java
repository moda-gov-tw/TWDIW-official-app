package gov.moda.dw.verifier.vc.web.rest;

import gov.moda.dw.verifier.vc.vo.Definition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PublicInfoResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicInfoResource.class);

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
}
