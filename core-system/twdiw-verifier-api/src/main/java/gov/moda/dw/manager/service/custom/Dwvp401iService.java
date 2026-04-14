package gov.moda.dw.manager.service.custom;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import gov.moda.dw.manager.domain.VPItem;
import gov.moda.dw.manager.domain.VPVerifyResult;
import gov.moda.dw.manager.repository.custom.CustomVPItemRepository;
import gov.moda.dw.manager.repository.custom.CustomVPVerifyResultRepository;
import gov.moda.dw.manager.service.dto.custom.Dwvp401iResDTO;
import gov.moda.dw.manager.service.dto.custom.ext.api.VerifierOid4vp101iRespDTO;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.web.rest.errors.DWException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class Dwvp401iService {

    private final CustomVPItemRepository customVPItemRepository;

    private final CustomVPVerifyResultRepository customVPVerifyResultRepository;

    private final RemoteApiService remoteApiService;

    /**
     * 取得於 offline 模式的自主揭露 qrcode
     * 
     * @param vpUid
     * @return
     */
    public Dwvp401iResDTO getOfflineQrcodeByVpUid(String vpUid) {
        log.debug("Request to get offline qrcode by vpUid : {}", vpUid);

        if (StringUtils.isBlank(vpUid)) {
            throw new DWException(StatusCode.DWVP_VPUID_NOT_EXISTS);
        }

        // 取得 businessId、serialNo
        int firstIndex = vpUid.indexOf("_");
        if (firstIndex < 0) {
            throw new DWException(StatusCode.DWVP_VPUID_NOT_VALID);
        }
        String businessId = vpUid.substring(0, firstIndex);
        String serialNo = vpUid.substring(firstIndex + 1, vpUid.length());

        // 查詢 VPItem 資料
        Optional<VPItem> vpItemOpt = customVPItemRepository.findBySerialNoAndBusinessId(serialNo, businessId);

        // 檢核 VPItem 是否存在
        if (vpItemOpt.isEmpty()) {
            throw new DWException(StatusCode.DWVP_VP_ITEM_NOT_FOUND);
        }

        // step1 產生 transactionId
        String transactionId = UUID.randomUUID().toString();

        // step2 呼叫 /api/qrcode 取得 QRCode/DeepLink
        VerifierOid4vp101iRespDTO verifierOid4vp101iRespDTO = remoteApiService.verifierOid4vp101i(vpUid, transactionId,
                null);

        // step3 寫入 verifyResult表
        VPItem vpItem = vpItemOpt.get();
        VPVerifyResult vpVerifyResult = new VPVerifyResult();
        vpVerifyResult.setTransactionId(transactionId);
        vpVerifyResult.setVpItemId(vpItem.getId());
        vpVerifyResult.setCrDatetime(Instant.now());
        customVPVerifyResultRepository.save(vpVerifyResult);

        // 建立 response
        Dwvp401iResDTO res = new Dwvp401iResDTO();
        res.setTransactionId(transactionId);
        res.setDeepLink(verifierOid4vp101iRespDTO.getAuthUri());
        return res;
    }

}
