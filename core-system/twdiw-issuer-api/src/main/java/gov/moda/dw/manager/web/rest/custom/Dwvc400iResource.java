package gov.moda.dw.manager.web.rest.custom;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.moda.dw.manager.service.custom.Dwvc400iService;

/**
 * 用於處理VC資料串接
 *
 */
@RestController
@RequestMapping("/api/getVcData")
public class Dwvc400iResource {

    private final Dwvc400iService dwvc400iService;

    public Dwvc400iResource(Dwvc400iService dwvc400iService) {
        this.dwvc400iService = dwvc400iService;
    }

    /**
     * 用於取得VC資料
     *
     * @param id
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping(path = "/{id}/.well-known/openid-credential-issuer", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getVcData(@PathVariable String id) throws JsonProcessingException {
        String result = dwvc400iService.getVcData(id);
        return ResponseEntity.ok().body(result);
    }

}
