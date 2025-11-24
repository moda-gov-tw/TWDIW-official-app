package gov.moda.dw.verifier.vc.web.rest;

import gov.moda.dw.verifier.vc.service.vp.PresentationServiceAsync;
import gov.moda.dw.verifier.vc.util.Tuple;
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

import java.util.List;

@RestController
@RequestMapping("/api")
public class PresentationResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PresentationResource.class);

    private final PresentationServiceAsync presentationServiceAsync;

    public PresentationResource(PresentationServiceAsync presentationServiceAsync) {
        this.presentationServiceAsync = presentationServiceAsync;
    }

    /**
     * validate presentation
     *
     * @param presentationList list of presentation
     * @return validate result
     */
    @PostMapping(path = "/presentation/validation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasAuthority('dwverifiervp101i')")
    public ResponseEntity<String> validate(@RequestBody List<String> presentationList) {
        LOGGER.debug("[presentation validate request] count={}", presentationList == null ? 0 : presentationList.size());

        Tuple.Pair<String, HttpStatus> result = presentationServiceAsync.validate(presentationList);

        LOGGER.debug("[HTTP status]:: {}", result.getB().toString());

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
