package gov.moda.dw.manager.service.custom;

import java.time.Instant;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import gov.moda.dw.manager.service.VCItemDataService;
import gov.moda.dw.manager.service.dto.DwIssuerVC402iResDTO;
import gov.moda.dw.manager.service.dto.DwissuerVC203iResp;
import gov.moda.dw.manager.service.dto.IssuerVC203iRes;
import gov.moda.dw.manager.service.dto.IssuerVC203iRes.IssuerVC203iInfoRes;
import gov.moda.dw.manager.service.dto.VC503iResp;
import gov.moda.dw.manager.service.dto.VCApiErrorResDTO;
import gov.moda.dw.manager.service.dto.custom.DwIssuerOidVci101iReq;
import gov.moda.dw.manager.service.dto.custom.DwIssuerOidVci101iRes;
import gov.moda.dw.manager.service.dto.custom.FindIssuerResDTO;
import gov.moda.dw.manager.service.dto.custom.frontend.api.VCSchemaResDto;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.type.SystemCode;
import gov.moda.dw.manager.util.RegexUtils;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import gov.moda.dw.manager.web.rest.errors.InternalErrorAlertException;

@Service
public class RemoteApiService {

    @Value("${dwfront.url}")
    private String dwfrontUrl;

    @Value("${issuer.oidvci-uri}")
    private String issuerOidVciUrl;

    @Value("${remote-url.issuer-vc}")
    private String issuerVcUrl;

    @Value("${issuer.dwissuer-vc-402i}")
    private String getDwIssuerVc402iUri;

    @Value("${issuer.dwissuer-oid4vci-101i}")
    private String getDwIssuerOidVci101iUri;

    @Value("${dwfront.dwfront-301i}")
    private String findIssuerURL;

    @Value("${remote-url.operation-manual}")
    private String operationManualUrl;

    private static final Logger log = LoggerFactory.getLogger(VCItemDataService.class);

    private RestClient client = RestClient.builder().build();

    public String issuerVc203i(String vcCid) {
        String urlPrefix = issuerVcUrl + "/api/credential/";
        ResponseEntity<String> stringResponseEntity = client.put().uri(urlPrefix + vcCid + "/revocation").retrieve().toEntity(String.class);
        return stringResponseEntity.getBody();
    }

    public DwissuerVC203iResp callApi203i(String vcCid, String action) {
        String urlPrefix = issuerVcUrl + "/api/credential/";

        ResponseEntity<DwissuerVC203iResp> stringResponseEntity = null;
        try {
            stringResponseEntity = client.put().uri(urlPrefix + vcCid + "/" + action).retrieve().toEntity(DwissuerVC203iResp.class);
        } catch (HttpClientErrorException | InternalServerError e) {
            VCApiErrorResDTO vcApiErrorRes = e.getResponseBodyAs(VCApiErrorResDTO.class);
            if (vcApiErrorRes == null) {
                throw new InternalErrorAlertException(
                    e.getResponseBodyAsString(),
                    e.getResponseBodyAsString() +
                    e.getStatusCode() +
                    ":resp body: " +
                    e.getResponseBodyAsString() +
                    ":" +
                    vcCid +
                    ":" +
                    action,
                    e.getResponseBodyAsString() +
                    e.getStatusCode() +
                    ":resp body: " +
                    e.getResponseBodyAsString() +
                    ":" +
                    vcCid +
                    ":" +
                    action
                );
            } else {
                DwissuerVC203iResp response = new DwissuerVC203iResp();
                response.setCode(vcApiErrorRes.getCode());
                response.setMessage(vcApiErrorRes.getMessage());

                return response;
            }
        } catch (Exception ex) {
            log.error("callApi203i-error {}", ex.getMessage(), ExceptionUtils.getStackTrace(ex));
            throw ex;
        }

        return stringResponseEntity.getBody();
    }

    public DwIssuerVC402iResDTO callApi402i(String transactionId) {
        DwIssuerVC402iResDTO response;
        RestTemplate restTemplate = new RestTemplate();
        String uri = getDwIssuerVc402iUri + "/" + transactionId;
        response = restTemplate.getForObject(uri, DwIssuerVC402iResDTO.class);
        return response;
    }

    // vc data qrcode
    public DwIssuerOidVci101iRes callApi101i(String nonce, String vcOrgId, DwIssuerOidVci101iReq reqContent) {
        DwIssuerOidVci101iRes response = null;

        RestTemplate restTemplate = new RestTemplate();
        String uri = getDwIssuerOidVci101iUri + "/" + vcOrgId + "/qr-code";
        response = restTemplate.postForObject(uri, reqContent, DwIssuerOidVci101iRes.class);

        return response;
    }

    public VC503iResp callApi503i(String get503iUri, String credentialType, String metadata) {
        VC503iResp response;

        try {
            RestTemplate restTemplate = new RestTemplate();
            String uri = get503iUri + "/" + credentialType;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> request = new HttpEntity<>(metadata, headers);
            response = restTemplate.postForObject(uri, request, VC503iResp.class);
            log.info("delete vc template: {}", response);
        } catch (HttpClientErrorException ex) {
            log.error("callApi503iForExternalCall-HttpServerErrorException  {}", ex.getMessage(), ExceptionUtils.getStackTrace(ex));
            VCApiErrorResDTO vcApiErrorRes = ex.getResponseBodyAs(VCApiErrorResDTO.class);
            throw new InternalErrorAlertException(
                vcApiErrorRes.getCode() + ":" + vcApiErrorRes.getMessage(),
                vcApiErrorRes.getMessage(),
                vcApiErrorRes.getCode()
            );
        } catch (Exception ex) {
            log.error("callApi503iForExternalCall-error {}", ex.getMessage(), ExceptionUtils.getStackTrace(ex));
            throw ex;
        }

        return response;
    }

    /**
     * 查詢已上鏈 VC Schema 列表(dwfront-304i)
     *
     * @param businessId
     * @return
     */
    public VCSchemaResDto getVCSchema(String businessId) {
        String url = dwfrontUrl + "/api/schema";
        try {
            // 移除所有標點符號 (P)、符號 (S)、分隔符 (Z)
            String apiBusinessId = this.removeSpecChar(businessId);

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url).queryParam("businessId",
                    apiBusinessId);
            String apiUrl = uriBuilder.toUriString();
            log.info("getVCSchema-url: {}", apiUrl);
            ResponseEntity<VCSchemaResDto> stringResponseEntity = client.get().uri(uriBuilder.build().toString())
                    .retrieve().toEntity(VCSchemaResDto.class);

            return stringResponseEntity.getBody();
        } catch (HttpClientErrorException ex) {
            log.error("getVCSchema-HttpServerErrorException  {}", ex.getMessage(), ExceptionUtils.getStackTrace(ex));
            VCApiErrorResDTO vcApiErrorRes = ex.getResponseBodyAs(VCApiErrorResDTO.class);
            throw new InternalErrorAlertException(
                vcApiErrorRes.getCode() + ":" + vcApiErrorRes.getMessage(),
                vcApiErrorRes.getMessage(),
                vcApiErrorRes.getCode()
            );
        }
    }

    public String getVCInfo(String url) {
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

    public FindIssuerResDTO getDIDInfoById(String did) {

        // 檢查 did 是否有值
        if (StringUtils.isAllBlank(did)) {
            throw new BadRequestAlertException(StatusCode.DID_INIT_FIND_ISSUER_ERROR.getCode(), "Not_Exist",
                    StatusCode.DID_INIT_FIND_ISSUER_ERROR.getMsg());
        }

        // 若 did 有值，則拿 did 的值呼叫 dwfront-301i 取得 issuer 相關資訊
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = findIssuerURL + "/" + did;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<FindIssuerResDTO> findIssuerResonse = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                FindIssuerResDTO.class
                );

        FindIssuerResDTO findIssuerResDTO = findIssuerResonse.getBody();
        if (null == findIssuerResDTO || findIssuerResDTO.getCode() != 0) {
            throw new BadRequestAlertException(StatusCode.DID_INIT_FIND_ISSUER_ERROR.getCode(), "Not_Exist",
                    StatusCode.DID_INIT_FIND_ISSUER_ERROR.getMsg());
        }
        return findIssuerResonse.getBody();
    }

    public String getVersionInfo(SystemCode systemCode) {
        String url = "";
        String managementInfoUrl = "/management/info";
        RestTemplate restTemplate = new RestTemplate();

        if (systemCode == SystemCode.OID4VCI) {
            url = issuerOidVciUrl + managementInfoUrl;
        } else if (systemCode == SystemCode.VC) {
            url = issuerVcUrl + managementInfoUrl;
        }

        return restTemplate.getForObject(url, String.class);
    }

    // 呼叫 VC 302i api
    public IssuerVC203iRes callApiVc203i(String vcCid, String action) {
        String urlPrefix = issuerVcUrl + "/api/credential/";
        IssuerVC203iRes response = new IssuerVC203iRes();

        try {
            ResponseEntity<DwissuerVC203iResp> entity = client.put().uri(urlPrefix + vcCid + "/" + action).retrieve()
                    .toEntity(DwissuerVC203iResp.class);

            // 處理成功回傳
            DwissuerVC203iResp body = entity.getBody();
            if (null != body) {
                log.info("callApiVc203i success action : {}, vcCid : {}, credentialStatus : {}", action, vcCid,
                        body.getCredentialStatus());
                response.setCredentialStatus(body.getCredentialStatus());
            }

            return response;
        } catch (HttpClientErrorException | InternalServerError e) {
            log.warn("callApiVc203i error action : {}, message : {}", action, e.getResponseBodyAsString());

            VCApiErrorResDTO vcApiErrorRes = e.getResponseBodyAs(VCApiErrorResDTO.class);
            if (null == vcApiErrorRes) {
                throw e;
            } else {
                IssuerVC203iInfoRes info = new IssuerVC203iInfoRes();
                info.setCid(vcCid);

                response.setCode(vcApiErrorRes.getCode());
                response.setMessage(vcApiErrorRes.getMessage());
                response.setInfo(info);
                return response;
            }
        } catch (Exception ex) {
            log.warn("callApiVc203i unexpected error, action={}, vcCid={}, message={}", action, vcCid,
                    ExceptionUtils.getStackTrace(ex));
            throw ex;
        }
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

    /**
     * 移除所有標點符號 (P)、符號 (S)、分隔符 (Z)
     * 
     * @param text
     * @return
     */
    private String removeSpecChar(String text) {
        return RegexUtils.matchWithTimeout("\\p{P}|\\p{S}|\\p{Z}").matcher(text).replaceAll("").trim();
    }
}
