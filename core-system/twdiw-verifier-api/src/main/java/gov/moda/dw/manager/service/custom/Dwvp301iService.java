package gov.moda.dw.manager.service.custom;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.moda.dw.manager.domain.VPItem;
import gov.moda.dw.manager.service.custom.common.DWVPService;
import gov.moda.dw.manager.service.dto.custom.Dwvp301iCallbackReqDTO;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.web.rest.errors.DWException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class Dwvp301iService {

    private final RemoteApiService remoteApiService;

    private final DWVPService dwvpService;

    private final DwSandBoxVP401WService dwSandBoxVP401WService;

    private final ObjectMapper objectMapper;

    /**
     * VP 驗證結果通道處理 callback 驗證端端服務
     *
     * @param request callback 請求
     * @throws JsonProcessingException 
     */
    public void processCallback(Dwvp301iCallbackReqDTO request) {
        // 檢核
        validateCallbackRequest(request);

        // 透過 vpUid 取得 VPItem 資料
        Optional<VPItem> vpItemOpt = dwvpService.getVpItemOpt(request.getVpUid());

        if (vpItemOpt.isEmpty()) {
            throw new DWException(StatusCode.DWVP_VP_ITEM_NOT_FOUND);
        }

        VPItem vpItem = vpItemOpt.get();
        String callbackUrl = vpItem.getCallbackUrl();

        if (StringUtils.isBlank(callbackUrl)) {
            throw new DWException(StatusCode.DWVP_CALLBACK_URL_NOT_EXISTS);
        }

        // 轉發請求到 callbackUrl
        forwardCallbackRequest(callbackUrl, request);
    }

    /**
     * 轉發 callback 請求到指定的 URL
     *
     * @param callbackUrl 目標 URL
     * @param request     callback 請求資料
     */
    private void forwardCallbackRequest(String callbackUrl, Dwvp301iCallbackReqDTO request) {
        try {
            // format request
            String payloadString = objectMapper.writeValueAsString(request);
            JsonNode apiRequest = dwSandBoxVP401WService.formatPayloadResult(request.getTransactionId(), payloadString, null);

            // callback to callbackUrl
            ResponseEntity<String> response = remoteApiService.postRequest(callbackUrl, apiRequest);

            // 檢查回應狀態碼
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("Callback request failed with status: {}", response.getStatusCode());
                throw new DWException(StatusCode.DWVP_CALLBACK_REQUEST_FAILED);
            }
        } catch (Exception e) {
            log.warn("Callback failed: {}", e.getMessage());
            throw new DWException(StatusCode.DWVP_CALLBACK_REQUEST_FAILED);
        }
    }

    /**
     * 驗證 callback 請求參數
     *
     * @param request 請求參數
     */
    private void validateCallbackRequest(Dwvp301iCallbackReqDTO request) {
        if (StringUtils.isBlank(request.getTransactionId())) {
            throw new DWException(StatusCode.DWVP_TRANSACTIONID_NOT_FOUND);
        }

        if (StringUtils.isBlank(request.getVpUid())) {
            throw new DWException(StatusCode.DWVP_VPUID_NOT_EXISTS);
        }
    }
}
