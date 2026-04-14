package gov.moda.dw.manager.service.custom.common;

import java.util.Optional;

import org.springframework.stereotype.Service;

import gov.moda.dw.manager.domain.VPItem;
import gov.moda.dw.manager.repository.custom.CustomVPItemRepository;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.web.rest.errors.DWException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DWVP 共用服務
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DWVPService {

    private final CustomVPItemRepository customVPItemRepository;

    /**
     * 透過 vpUid 取得 VPItem 資料
     * 
     * @param vpUid
     * @return VPItem
     */
    public Optional<VPItem> getVpItemOpt(String vpUid) {
        int firstIndex = vpUid.indexOf("_");
        if (firstIndex < 0) {
            throw new DWException(StatusCode.DWVP_VPUID_NOT_VALID);
        }
        String businessId = vpUid.substring(0, firstIndex);
        String serialNo = vpUid.substring(firstIndex + 1);
        return customVPItemRepository.findBySerialNoAndBusinessId(serialNo, businessId);
    }
}
