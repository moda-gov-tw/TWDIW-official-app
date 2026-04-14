package gov.moda.dw.manager.web.rest.custom;

import gov.moda.dw.manager.domain.VCItem;
import gov.moda.dw.manager.repository.custom.CustomVCItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Display VC Card Cover
 */
@Slf4j
@RestController
@RequestMapping("/api/images")
public class DwSandBoxVC901WExternalResource {

    private final CustomVCItemRepository vCItemRepository;

    public DwSandBoxVC901WExternalResource(CustomVCItemRepository vCItemRepository) {
        this.vCItemRepository = vCItemRepository;
    }

    @GetMapping(value = "/cover/{bussiness_id}/{vc_id}")
    public ResponseEntity<String> getImage(@PathVariable("bussiness_id") String bussinessId, @PathVariable("vc_id") String vcId) {
        // 1. 從資料庫查詢圖片的 Base64 字串
        VCItem vcItem = vCItemRepository.findBySerialNoAndBusinessId(vcId, bussinessId); // 模擬從資料庫查詢

        if (vcItem == null) return ResponseEntity.notFound().build();

        String base64Image = vcItem.getCardCover();

        if (base64Image == null || base64Image.isEmpty()) return ResponseEntity.notFound().build();

        // 2. 直接回傳 Base64 Data
        return ResponseEntity.ok(base64Image);
    }
}
