package gov.moda.dw.issuer.oidvci.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import gov.moda.dw.issuer.oidvci.config.ErrorCodeConfiguration;
import gov.moda.dw.issuer.oidvci.web.rest.CredentialIssuer;
import gov.moda.dw.issuer.oidvci.web.rest.errors.HTTPConnectException;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;

@Service
public class HTTPService {

	private static final Logger log = LoggerFactory.getLogger(HTTPService.class);

	@Autowired
    private RestTemplate restTemplate;

	private JSONObject makeReturnObj(int error_code) {

		JSONObject rtn_obj = new JSONObject();
		rtn_obj.put("resp_code", error_code);
		rtn_obj.put("resp_message", ErrorCodeConfiguration.getErrorMessage(error_code));

		return rtn_obj;
	}

	private JSONObject makeReturnObj(int error_code, String extra_err_msg) {

		JSONObject rtn_obj = new JSONObject();
		rtn_obj.put("resp_code", error_code);
		rtn_obj.put("resp_message", ErrorCodeConfiguration.getErrorMessage(error_code) + extra_err_msg);
		return rtn_obj;
	}

	public String sendPostRequest(String url, String request_str) {

		int error_code;
		String error_message;
        // 設置請求頭
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // 包裝請求體和頭部
        HttpEntity<String> entity = new HttpEntity<>(request_str, headers);

        try {
            // 發送 POST 請求
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class);

            // 根據狀態碼做出相應處理
            int statusCode = response.getStatusCode().value();
            if (statusCode == 200 || statusCode == 201) {
                // 成功處理，回傳結果
                log.info("POST Success: {}", response.getBody());
                error_code = 10000;
            	JSONObject ret_json = makeReturnObj(error_code);
            	ret_json.put("response_body", response.getBody());
                return ret_json.toJSONString(JSONStyle.LT_COMPRESS);
            } else {
                // 其他 2xx 狀態碼
            	log.info("Unexpected status: {}, response: {}",statusCode, response.getBody());
            	error_code = 11020;
            	return makeReturnObj(error_code).toJSONString(JSONStyle.LT_COMPRESS);
            }
        } catch (HttpClientErrorException e) {
            // 處理 4xx 錯誤
            int statusCode = e.getStatusCode().value();
            if (statusCode == 400) {
            	log.info("Bad Request: {}", e.getResponseBodyAsString());
            	error_code = 11020;
            	return makeReturnObj(error_code, String.valueOf(statusCode)).toJSONString(JSONStyle.LT_COMPRESS);
            } else if (statusCode == 401) {
            	log.info("Unauthorized: {}", e.getResponseBodyAsString());
            	error_code = 11020;
            	return makeReturnObj(error_code, String.valueOf(statusCode)).toJSONString(JSONStyle.LT_COMPRESS);
            } else if (statusCode == 404) {
            	log.info("Not Found: {}", e.getResponseBodyAsString());
            	error_code = 11020;
            	return makeReturnObj(error_code, String.valueOf(statusCode)).toJSONString(JSONStyle.LT_COMPRESS);

            } else {
            	log.info("Client Error: {}, response: {}", statusCode, e.getResponseBodyAsString());
            	error_code = 11020;
            	return makeReturnObj(error_code, String.valueOf(statusCode)).toJSONString(JSONStyle.LT_COMPRESS);
            }

        } catch (HttpServerErrorException e) {
            // 處理 5xx 錯誤
            int statusCode = e.getStatusCode().value();
            if (statusCode == 500) {
            	log.info("VC Server Error: {}", e.getResponseBodyAsString());
            	error_code = 11021;
            	return makeReturnObj(error_code).toJSONString(JSONStyle.LT_COMPRESS);
            } else {
                log.info("VC Server Error: {}, response: {}", statusCode, e.getResponseBodyAsString());
                error_code = 11021;
                return makeReturnObj(error_code).toJSONString(JSONStyle.LT_COMPRESS);
            }

        } catch (ResourceAccessException e) {
            // 處理連接失敗
        	log.info("Connection Error: {}", e.getMessage());
        	error_code = 11022;
        	return makeReturnObj(error_code).toJSONString(JSONStyle.LT_COMPRESS);
        } catch (Exception e) {
            // 處理其他異常
        	log.info("Unknown Error: {}", e.getMessage());
        	error_code = 11500;
        	return makeReturnObj(error_code).toJSONString(JSONStyle.LT_COMPRESS);
        }
	}

    public String sendPutRequest(String url) {
        int error_code;

        try {
            // 確保 URL 乾淨
            url = url.trim().replace("\"", "");

            // 空的 PUT 請求，不送 header / 不送 body
            HttpEntity<Void> entity = new HttpEntity<>(null);

            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.PUT, entity, String.class);

            int statusCode = response.getStatusCodeValue();
            String body = response.getBody();
            log.info("PUT Response ({}): {}", statusCode, body);

            if (statusCode == 200 || statusCode == 201) {
                // 檢查 credentialStatus 欄位
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(body);
                JsonNode statusNode = root.get("credentialStatus");

                if (statusNode == null || !"REVOKED".equalsIgnoreCase(statusNode.asText())) {
                    error_code = 12010;
                    String errorMessage = "Invalid or missing credentialStatus";
                    log.error("Credential Status Check Failed: {}", errorMessage);
                    return makeReturnObj(error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS);
                }

                // 一切正常
                error_code = 10000;
                JSONObject ret_json = makeReturnObj(error_code);
                ret_json.put("response_body", body);
                return ret_json.toJSONString(JSONStyle.LT_COMPRESS);
            } else {
                error_code = 11020;
                log.info("Unexpected status: {}, response: {}", statusCode, body);
                return makeReturnObj(error_code).toJSONString(JSONStyle.LT_COMPRESS);
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            int statusCode = e.getStatusCode().value();
            log.info("HTTP Error {}: {}", statusCode, e.getResponseBodyAsString());
            error_code = (statusCode >= 500) ? 11021 : 11020;
            return makeReturnObj(error_code, String.valueOf(statusCode)).toJSONString(JSONStyle.LT_COMPRESS);

        } catch (ResourceAccessException e) {
            log.info("Connection Error: {}", e.getMessage());
            error_code = 11022;
            return makeReturnObj(error_code).toJSONString(JSONStyle.LT_COMPRESS);

        } catch (Exception e) {
            log.info("Unknown Error: {}", e.getMessage());
            error_code = 11500;
            return makeReturnObj(error_code).toJSONString(JSONStyle.LT_COMPRESS);
        }
    }

}
