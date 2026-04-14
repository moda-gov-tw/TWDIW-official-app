package gov.moda.dw.manager.monitor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.ApiVersion;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.WebServerNamespace;
import org.springframework.boot.actuate.health.CompositeHealth;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.actuate.health.HealthEndpointGroups;
import org.springframework.boot.actuate.health.HealthEndpointWebExtension;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
@Primary
public class CustomIssuerHealthWebExtension extends HealthEndpointWebExtension {

    private static final Logger log = LoggerFactory.getLogger(CustomIssuerHealthWebExtension.class);

    @Autowired(required = false)
    private BuildProperties buildProperties;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${vc.internal.url}")
    private String vcInternalUrl;

    @Value("${oid4vci.internal.url}")
    private String oid4vciInternalUrl;

    public CustomIssuerHealthWebExtension(
        HealthContributorRegistry registry,
        HealthEndpointGroups groups
    ) {
        // timeout 給 30 秒，跟官方預設一致
        super(registry, groups, Duration.ofSeconds(30));
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public WebEndpointResponse<HealthComponent> health(
        ApiVersion apiVersion,
        WebServerNamespace serverNamespace,
        SecurityContext securityContext,
        boolean showDetails,
        String... path
    ) {
        WebEndpointResponse<HealthComponent> response =
            super.health(apiVersion, serverNamespace, securityContext, showDetails, path);

        if (buildProperties != null) {
            log.info("CustomIssuerHealthWebExtension: Health check triggered, twdiw-release={}, image-version={}",
                buildProperties.get("twdiw-release.version"),
                buildProperties.getVersion()
            );
        } else {
            log.info("CustomIssuerHealthWebExtension: Health check triggered, but buildProperties is null.");
        }

        HealthComponent body = response.getBody();

        if (body instanceof Health health) {
            // 建立新的 Health，details加入 release-version
            Health.Builder builder = Health.status(health.getStatus());

            // 保留原有的所有 details
            if (health.getDetails() != null) {
                health.getDetails().forEach(builder::withDetail);
            }

            // 建立 twdiw-release Map，包含所有服務的 twdiw-release
            Map<String, String> twdiwReleaseMap = new HashMap<>();

            // 建立 image-version Map，包含所有服務的 image-version
            Map<String, String> imageVersionMap = new HashMap<>();

            // 如果有 BuildProperties，加入 manager 服務資訊
            if (buildProperties != null) {
                twdiwReleaseMap.put("moda-digitalwallet-docker-issuer-manager-service", (String) buildProperties.get("twdiw-release.version"));
                imageVersionMap.put("moda-digitalwallet-docker-issuer-manager-service", buildProperties.getVersion());
            }

            // 取得 vc 和 oid4vci 服務的版本資訊
            Map<String, String> vcServiceInfo = getServiceHealthInfo(vcInternalUrl, "vc");
            if (vcServiceInfo != null) {
                if (vcServiceInfo.containsKey("twdiw-release")) {
                    twdiwReleaseMap.put("moda-digitalwallet-docker-issuer-vc-service", vcServiceInfo.get("twdiw-release"));
                }
                if (vcServiceInfo.containsKey("image-version")) {
                    imageVersionMap.put("moda-digitalwallet-docker-issuer-vc-service", vcServiceInfo.get("image-version"));
                }
            }

            Map<String, String> oid4vciServiceInfo = getServiceHealthInfo(oid4vciInternalUrl, "oid4vci");
            if (oid4vciServiceInfo != null) {
                if (oid4vciServiceInfo.containsKey("twdiw-release")) {
                    twdiwReleaseMap.put("moda-digitalwallet-docker-issuer-oid4vci-service", oid4vciServiceInfo.get("twdiw-release"));
                }
                if (oid4vciServiceInfo.containsKey("image-version")) {
                    imageVersionMap.put("moda-digitalwallet-docker-issuer-oid4vci-service", oid4vciServiceInfo.get("image-version"));
                }
            }

            // 將 twdiw-release 和 image-version 加入到 details 中
            builder.withDetail("twdiw-release", twdiwReleaseMap);
            builder.withDetail("image-version", imageVersionMap);

            return new WebEndpointResponse<>(builder.build(), response.getStatus());
        } else if (body instanceof CompositeHealth compositeHealth) {
            // 處理 CompositeHealth 的情況
            Health.Builder builder = Health.status(compositeHealth.getStatus());

            // 保留原有的所有 details
            if (compositeHealth.getDetails() != null) {
                compositeHealth.getDetails().forEach(builder::withDetail);
            }

            // 建立 twdiw-release Map，包含所有服務的 twdiw-release
            Map<String, String> twdiwReleaseMap = new HashMap<>();

            // 建立 image-version Map，包含所有服務的 image-version
            Map<String, String> imageVersionMap = new HashMap<>();

            // 如果有 BuildProperties，加入 manager 服務資訊
            if (buildProperties != null) {
                twdiwReleaseMap.put("moda-digitalwallet-docker-issuer-manager-service", (String) buildProperties.get("twdiw-release.version"));
                imageVersionMap.put("moda-digitalwallet-docker-issuer-manager-service", buildProperties.getVersion());
            }

            // 取得 vc 和 oid4vci 服務的版本資訊
            Map<String, String> vcServiceInfo = getServiceHealthInfo(vcInternalUrl, "vc");
            if (vcServiceInfo != null) {
                if (vcServiceInfo.containsKey("twdiw-release")) {
                    twdiwReleaseMap.put("moda-digitalwallet-docker-issuer-vc-service", vcServiceInfo.get("twdiw-release"));
                }
                if (vcServiceInfo.containsKey("image-version")) {
                    imageVersionMap.put("moda-digitalwallet-docker-issuer-vc-service", vcServiceInfo.get("image-version"));
                }
            }

            Map<String, String> oid4vciServiceInfo = getServiceHealthInfo(oid4vciInternalUrl, "oid4vci");
            if (oid4vciServiceInfo != null) {
                if (oid4vciServiceInfo.containsKey("twdiw-release")) {
                    twdiwReleaseMap.put("moda-digitalwallet-docker-issuer-oid4vci-service", oid4vciServiceInfo.get("twdiw-release"));
                }
                if (oid4vciServiceInfo.containsKey("image-version")) {
                    imageVersionMap.put("moda-digitalwallet-docker-issuer-oid4vci-service", oid4vciServiceInfo.get("image-version"));
                }
            }

            // 將 twdiw-release 和 image-version 加入到 details 中
            builder.withDetail("twdiw-release", twdiwReleaseMap);
            builder.withDetail("image-version", imageVersionMap);

            return new WebEndpointResponse<>(builder.build(), response.getStatus());
        }

        return response;
    }

    /**
     * 呼叫服務的 /management/health API 並取得 twdiw-release 和 image-version
     *
     * @param baseUrl 服務的基礎 URL
     * @param serviceName 服務名稱（用於日誌）
     * @return 包含 twdiw-release 和 image-version 的 Map，如果取得失敗則返回 null
     */
    private Map<String, String> getServiceHealthInfo(String baseUrl, String serviceName) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            log.warn("Base URL for {} service is not configured", serviceName);
            return null;
        }
        try {
            String healthUrl = baseUrl + "/management/health";
            log.debug("Calling health endpoint for {} service: {}", serviceName, healthUrl);
            ResponseEntity<String> response = restTemplate.getForEntity(healthUrl, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode detailsNode = jsonNode.get("details");
                if (detailsNode != null) {
                    Map<String, String> serviceInfo = new HashMap<>();

                    // 取得 twdiw-release
                    if (detailsNode.has("twdiw-release")) {
                        String twdiwRelease = detailsNode.get("twdiw-release").asText();
                        serviceInfo.put("twdiw-release", twdiwRelease);
                    } else {
                        log.warn("Health response for {} service does not contain twdiw-release in details", serviceName);
                    }

                    // 取得 image-version
                    if (detailsNode.has("image-version")) {
                        String imageVersion = detailsNode.get("image-version").asText();
                        serviceInfo.put("image-version", imageVersion);
                    } else {
                        log.warn("Health response for {} service does not contain image-version in details", serviceName);
                    }

                    if (!serviceInfo.isEmpty()) {
                        log.debug("Successfully retrieved health info for {} service: {}", serviceName, serviceInfo);
                        return serviceInfo;
                    } else {
                        log.warn("Health response for {} service does not contain required fields in details", serviceName);
                    }
                } else {
                    log.warn("Health response for {} service does not contain details", serviceName);
                }
            } else {
                log.warn("Health endpoint for {} service returned non-2xx status: {}", serviceName, response.getStatusCode());
            }
        } catch (Exception ex) {
            log.warn("Failed to retrieve health info for {} service: {}", serviceName, ex.getMessage());
        }
        return null;
    }
}
