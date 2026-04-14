package gov.moda.dw.manager.service.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import gov.moda.dw.manager.domain.VCItem;
import gov.moda.dw.manager.repository.custom.CustomVCItemRepository;
import gov.moda.dw.manager.service.dto.custom.Dwvc101iResDTO;
import gov.moda.dw.manager.service.dto.custom.Dwvc101iVcItemsDTO;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.HttpXxxErrorExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 提供發行端模板相關資料
 * 負責取得發行端模板相關資料
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class Dwvc101iService {

    // 取得發行端相關資料
    private final CustomVCItemRepository customVCItemRepository;

    /**
     * 提供發行端模板相關資料
     * 
     * @param vcUid 統編_模板代碼
     * @return Dwvc101iResDTO 取得發行端模板相關資料
     */
    public Dwvc101iResDTO getExtVcData(String vcUid) {
        log.debug("Request to get VCItemData By vcUid : {}", vcUid);

        // 建立回應物件
        Dwvc101iResDTO dwvc101iResDTO = new Dwvc101iResDTO();

        // 取得 VC 欄位資料
        List<Dwvc101iVcItemsDTO> dataList = null;

        // 接收資料庫 VC 回傳的資料 List
        List<VCItem> vcItemList = null;

        // app 開啟網頁方式 1:外開網頁 2:webView
        List<String> type = Arrays.asList("1", "2");
        
        // 非暫存 vcUid
        boolean isTemp = false;

        if (!StringUtils.contains(vcUid, "_") && !StringUtils.equals("all", vcUid)) {
            throw HttpXxxErrorExceptionHandler.genHttpClientErrorException(HttpStatus.BAD_REQUEST,
                    StatusCode.DWVP_VCUID_NOT_VALID.getCode(), StatusCode.DWVP_VCUID_NOT_VALID.getMsg());
        }

        if (StringUtils.equals("all", vcUid)) {
            
            // 查詢全部的VC欄位資料(用Type)
            vcItemList = customVCItemRepository.findByIsTempAndTypeIn(isTemp, type);
        } else {
            // 分割 vcUid ， vcUid 裡的統編
            String businessId = StringUtils.split(vcUid, "_")[0];

            // 分割 vcUid ， vcUid 裡的模板代號
            String serialNo = StringUtils.substringAfter(vcUid, "_");

            // 單筆查詢
            vcItemList = customVCItemRepository.findBySerialNoAndBusinessIdAndIsTempAndTypeIn(serialNo, businessId, isTemp, type);
        }

        // 查無模板資料
        if (CollectionUtils.isEmpty(vcItemList)) {
            throw HttpXxxErrorExceptionHandler.genHttpClientErrorException(HttpStatus.BAD_REQUEST,
                    StatusCode.DWVP_VC_ITEM_NOT_FOUND.getCode(), StatusCode.DWVP_VC_ITEM_NOT_FOUND.getMsg());
        }

        // 取得 VC 欄位資料 List
        dataList = getVCItemDatas(vcItemList);
        dwvc101iResDTO.setVcItems(dataList);

        return dwvc101iResDTO;
    }

    /**
     * 取得 VC 欄位資料
     * 
     * @param vcItemList
     * @return dataList
     */
    private List<Dwvc101iVcItemsDTO> getVCItemDatas(List<VCItem> vcItemList) {
        log.debug("Request to get VCItemDataList By vcUid : {}", vcItemList);

        // 建立 VC 資料欄位
        Dwvc101iVcItemsDTO dwvc101iVcItemsDTO = null;

        // 取得 VC 欄位資料
        List<Dwvc101iVcItemsDTO> dataList = new ArrayList<>();

        for (VCItem vcItem : vcItemList) {
            // 建立回傳物件
            dwvc101iVcItemsDTO = new Dwvc101iVcItemsDTO();

            String vcUid = vcItem.getBusinessId() + "_" + vcItem.getSerialNo();
            // 回傳 VC 欄位資料
            dwvc101iVcItemsDTO.setVcUid(vcUid);
            dwvc101iVcItemsDTO.setName(vcItem.getName());
            dwvc101iVcItemsDTO.setType(vcItem.getType());
            dwvc101iVcItemsDTO.setIssuerServiceUrl(vcItem.getIssuerServiceUrl());

            dataList.add(dwvc101iVcItemsDTO);
        }
        return dataList;
    }

}
