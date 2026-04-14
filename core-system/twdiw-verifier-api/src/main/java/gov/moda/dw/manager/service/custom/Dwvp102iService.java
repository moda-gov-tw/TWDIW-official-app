package gov.moda.dw.manager.service.custom;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import gov.moda.dw.manager.domain.VPItem;
import gov.moda.dw.manager.repository.custom.CustomVPItemRepository;
import gov.moda.dw.manager.service.dto.custom.Dwvp102iResDTO;
import gov.moda.dw.manager.service.dto.custom.Dwvp102iResDTO.Dwvp102iVpItemResDTO;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.web.rest.errors.DWException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class Dwvp102iService {

    private final CustomVPItemRepository customVPItemRepository;

    /**
     * 提供 VP 驗證端 offline 相關資料
     * 
     * @return
     */
    public Dwvp102iResDTO getAllOfflineVPItemData() {
        log.debug("Request to get all offline VPItemData");

        // 查詢所有 offline VPItem 資料
        List<VPItem> offlineList = customVPItemRepository.findByIsOfflineTrue();
        if (CollectionUtils.isEmpty(offlineList)) {
            throw new DWException(StatusCode.DWVP_VP_ITEM_OFFLINE_NOT_FOUND);
        }

        // 建立 vpItems
        List<Dwvp102iVpItemResDTO> vpItems = offlineList.stream().map(vpItem -> {
            Dwvp102iVpItemResDTO res = new Dwvp102iVpItemResDTO();
            res.setVpUid(vpItem.getBusinessId() + "_" + vpItem.getSerialNo());
            res.setName(vpItem.getName());
            res.setIsStatic(vpItem.getIsStatic());
            res.setIsOffline(vpItem.getIsOffline());
            return res;
        }).toList();

        // 建立 response
        Dwvp102iResDTO res = new Dwvp102iResDTO();
        res.setVpItems(vpItems);
        return res;
    }

}
