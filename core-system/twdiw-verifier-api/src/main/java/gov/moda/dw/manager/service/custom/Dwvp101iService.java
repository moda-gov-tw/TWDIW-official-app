package gov.moda.dw.manager.service.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.moda.dw.manager.domain.VPItem;
import gov.moda.dw.manager.service.custom.common.DWVPService;
import gov.moda.dw.manager.service.dto.custom.Dwvp101iResDTO;
import gov.moda.dw.manager.service.dto.custom.Dwvp101iResDTO.Dwvp101iResFieldsDTO;
import gov.moda.dw.manager.service.dto.custom.Dwvp101iResDTO.Dwvp101iVpItemResDTO;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.web.rest.errors.DWException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class Dwvp101iService {

    private final DWVPService dwvpService;

    private final ObjectMapper objectMapper;

    public Dwvp101iResDTO getVPItemDataByVpUid(String vpUid) {

        // 查詢 VPItem 資料
        Optional<VPItem> vpItemOpt = dwvpService.getVpItemOpt(vpUid);
        if (vpItemOpt.isEmpty()) {
            throw new DWException(StatusCode.DWVP_VP_ITEM_NOT_FOUND);
        }
        VPItem vpItem = vpItemOpt.get();

        // 建立回應資料
        Dwvp101iResDTO res = new Dwvp101iResDTO();
        List<Dwvp101iVpItemResDTO> vpItems = new ArrayList<>();
        Dwvp101iVpItemResDTO vpItemDto = new Dwvp101iVpItemResDTO();
        vpItemDto.setVpUid(vpItem.getBusinessId() + "_" + vpItem.getSerialNo());
        vpItemDto.setName(vpItem.getName());
        vpItemDto.setVerifierServiceUrl(vpItem.getVerifierServiceUrl());
        vpItemDto.setIsStatic(vpItem.getIsStatic());
        vpItemDto.setIsOffline(vpItem.getIsOffline());

        Dwvp101iResFieldsDTO fieldInfo = this.jsonToObject(vpItem.getFieldInfo(),
                new TypeReference<Dwvp101iResFieldsDTO>() {
                });
        if (fieldInfo != null) {
            vpItemDto.setCustom(fieldInfo);
        } else {
            Dwvp101iResFieldsDTO emptyCustom = new Dwvp101iResFieldsDTO();
            emptyCustom.setFields(null);
            vpItemDto.setCustom(emptyCustom);
        }

        vpItems.add(vpItemDto);
        res.setVpItems(vpItems);

        return res;
    }

    /**
     * 將 json 轉為 T 物件
     * 
     * @param <T>
     * @param json
     * @param typeRef * @return
     */
    private <T> T jsonToObject(String json, TypeReference<T> typeRef) {
        try {
            if (StringUtils.isBlank(json)) {
                return null;
            }
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            throw new DWException(StatusCode.DWVP_FIELD_INFO_PARSE_ERROR);
        }
    }

}
