package gov.moda.dw.manager.service.custom;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import gov.moda.dw.manager.domain.outside.vdr.category.CategoryVdrResp;
import gov.moda.dw.manager.service.dto.custom.FindVerifierResDTO;
import gov.moda.dw.manager.service.dto.custom.ext.api.VPApiErrorResDTO;
import gov.moda.dw.manager.service.dto.custom.ext.api.VerifierOid4vp101iRespDTO;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.type.SystemCode;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import gov.moda.dw.manager.web.rest.errors.DWException;

@Service
public class RemoteApiService {
    private final Logger log = LoggerFactory.getLogger(RemoteApiService.class);
    @Value("${remote-url.verifier-oid4vp}")
    private String verifierOid4vpUrl;
    
    @Value("${remote-url.verifier-vp}")
    private String verifierVpUrl;

    @Value("${remote-url.vdr-issuer-list}")
    private String vdrIssuerList;

    @Value("${dwfront.dwfront-301i}")
    private String findVerifierURL;
    
    @Value("${remote-url.operation-manual}")
    private String operationManualUrl;

    private RestClient client = RestClient.builder().build();

    public VerifierOid4vp101iRespDTO verifierOid4vp101i(String ref, String transactionId, String callback) {
        String url = verifierOid4vpUrl + "/api/oidvp/qr-code";
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                    .queryParam("ref", ref)
                    .queryParam("transaction_id", transactionId);
            if (org.apache.commons.lang3.StringUtils.isNotBlank(callback)) {
                builder.queryParam("callback", callback);
            }
            ResponseEntity<VerifierOid4vp101iRespDTO> stringResponseEntity = client.get()
                    .uri(builder.build().toString()).retrieve().toEntity(VerifierOid4vp101iRespDTO.class);
            return stringResponseEntity.getBody();
        } catch (HttpClientErrorException | InternalServerError e) {
            VPApiErrorResDTO vpApiErrorRes = e.getResponseBodyAs(VPApiErrorResDTO.class);
            if (null == vpApiErrorRes) {
                String errorBody = e.getResponseBodyAsString();
                throw new RuntimeException(errorBody);
            } else {
                VerifierOid4vp101iRespDTO response = new VerifierOid4vp101iRespDTO();
                response.setCode(vpApiErrorRes.getCode());
                response.setMessage(vpApiErrorRes.getMessage());

                return response;
            }
        }
    }

    public String verifierOid4vp301i(String transactionId, String responseCode){
        String url = verifierOid4vpUrl+"/api/oidvp/result";
        // responseCode is optional
        try {
            ResponseEntity<String> stringResponseEntity = client
                    .post()
                    .uri(url)
                    .headers(httpHeaders -> {
                        httpHeaders.add("transaction-id", transactionId);

                        if (StringUtils.hasText(responseCode)) {
                            httpHeaders.add("response-code", responseCode);
                        }
                    })
                    .retrieve()
                    .toEntity(String.class);
            return stringResponseEntity.getBody();
        } catch (HttpClientErrorException | InternalServerError e) {
            VPApiErrorResDTO vpApiErrorRes = e.getResponseBodyAs(VPApiErrorResDTO.class);
            if (null == vpApiErrorRes) {
                String errorBody = e.getResponseBodyAsString();
                throw new RuntimeException(errorBody);
            } else {
                throw new DWException(vpApiErrorRes.getCode(), vpApiErrorRes.getMessage());
            }
        }
    }

    public CategoryVdrResp getCategory(){
        String url = vdrIssuerList;
        log.info("getVDR-Issuer-list-url:{}",url);
        try {
            ResponseEntity<CategoryVdrResp> stringResponseEntity = client.get()
                .uri(UriComponentsBuilder.fromUriString(url).build().toString())
                .retrieve().toEntity(CategoryVdrResp.class);
            return stringResponseEntity.getBody();
        }catch (HttpClientErrorException e){
            String errorBody = e.getResponseBodyAsString();
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new BadRequestAlertException("Call RemoteApiService ApiError", errorBody, "RemoteApiService ApiResponse not found error in getCategory()");
            }else{
                throw new RuntimeException(errorBody);
            }
        }
    }

    public String getVCInfo(String url){
        log.info("getVDR-Issuer-metadata-url:{}",url);
        try {
            ResponseEntity<String> stringResponseEntity = client.get()
                .uri(UriComponentsBuilder.fromUriString(url).build().toString())
                .retrieve().toEntity(String.class);
            return stringResponseEntity.getBody();
        }catch (HttpClientErrorException e){
            String errorBody = e.getResponseBodyAsString();
            if(   errorBody.contains("credential_issuer_meta is empty or null") || errorBody.contains("12006")
               || errorBody.contains("No data found matching the criteria")     || errorBody.contains("11901") ){ // 無資料回傳空陣列
                return "{\"credential_configurations_supported\":{}}";
            }else if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new BadRequestAlertException("Call RemoteApiService ApiError", errorBody, "RemoteApiService ApiResponse not found error in getVCInfo()");
            }else{
                throw new RuntimeException(errorBody);
            }
        }
    }
    

    public FindVerifierResDTO getDIDInfoById(String did) {
        // 檢查 did 是否有值
        if(org.apache.commons.lang3.StringUtils.isBlank(did)) {
            throw new BadRequestAlertException(StatusCode.DID_INIT_FIND_ISSUER_ERROR.getCode(), "Not_Exist",
                    StatusCode.DID_INIT_FIND_ISSUER_ERROR.getMsg());
        }

        // 若 did 有值，則拿 did 的值呼叫 dwfront-301i 取得 verifier 相關資訊
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = findVerifierURL + "/" + did;
        log.info("getDIDInfoById URL: {}", url);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<FindVerifierResDTO> findVerifierResponse = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            FindVerifierResDTO.class
        );

        FindVerifierResDTO findVerifierResDTO = findVerifierResponse.getBody();
        if (null == findVerifierResDTO || findVerifierResDTO.getCode() != 0) {
            throw new BadRequestAlertException(StatusCode.DID_INIT_FIND_ISSUER_ERROR.getCode(), "Not_Exist",
                    StatusCode.DID_INIT_FIND_ISSUER_ERROR.getMsg());
        }

        return findVerifierResponse.getBody();
    }

    /**
     * 通用的 POST 請求方法
     *
     * @param url 目標 URL
     * @param requestBody 請求體
     * @return ResponseEntity<String>
     * @throws HttpClientErrorException 當發生 4xx 錯誤時拋出
     * @throws Exception 當發生其他錯誤時拋出
     */
    public ResponseEntity<String> postRequest(String url, Object requestBody) throws HttpClientErrorException {
        log.info("Sending POST request to: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            entity,
            String.class
        );

        log.info("POST request completed with status: {}", response.getStatusCode());
        return response;
    }
    
    /**
     * 取得版本資訊
     * 
     * @param systemCode 系統類型
     * @return 版本內容
     */
    public String getVersionInfo(SystemCode systemCode) {
        String url = "";
        String managementInfoUrl = "/management/info";
        RestTemplate restTemplate = new RestTemplate();

        if (systemCode == SystemCode.OID4P) {
            url = verifierOid4vpUrl + managementInfoUrl;
        } else if (systemCode == SystemCode.VP) {
            url = verifierVpUrl + managementInfoUrl;
        }

        return restTemplate.getForObject(url, String.class);
    }
    
    /**
     * 取得操作手冊
     * 
     * @return
     */
    public byte[] getManualFileBytes() {
        long currentTime = Instant.now().getEpochSecond();
        String url = operationManualUrl + "?ver=" + currentTime;
        
        return client.get()
                .uri(url)
                .retrieve()
                .body(byte[].class);
        
    }

}
