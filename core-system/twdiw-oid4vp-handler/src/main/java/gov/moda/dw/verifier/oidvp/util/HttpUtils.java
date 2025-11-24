package gov.moda.dw.verifier.oidvp.util;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class HttpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    private final RestTemplate restTemplate;

    private HttpUtils(HttpClient httpClient) {
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    public <T> ResponseEntity<T> doPost(String url, Object body, HttpHeaders headers, Class<T> responseType) {
        return doPost(url, body, headers, responseType, (Object) null);
    }

    private <T> ResponseEntity<T> doPost(String url, Object body, HttpHeaders headers, Class<T> responseType, Object... uriVariables) {
        HttpEntity<Object> httpEntity;
        if (headers == null) {
            HttpHeaders defaultHeaders = new HttpHeaders();
            defaultHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpEntity = new HttpEntity<>(body, defaultHeaders);
        } else {
            httpEntity = new HttpEntity<>(body, headers);
        }

        try {
            return restTemplate.postForEntity(url, httpEntity, responseType, uriVariables);
        } catch (RestClientResponseException e) {
            LOGGER.warn("HTTP -- POST request error, path: {}", url);
            LOGGER.warn("HTTP -- status: {}", e.getStatusCode());
            LOGGER.warn("HTTP -- message: {}", e.getMessage());
            throw e;
        } catch (RestClientException e) {
            LOGGER.warn("HTTP -- POST request error, path: {}", url);
            LOGGER.warn("HTTP -- message: {}", e.getMessage());
            throw e;
        }
    }

    public static class CustomHttpRequestRetryStrategy extends DefaultHttpRequestRetryStrategy {

        private static final List<Class<? extends IOException>> nonRetriableIOExceptionClasses = Arrays.asList(UnknownHostException.class, ConnectException.class, NoRouteToHostException.class, SSLException.class);
        private static final List<Integer> retriableCodes = Arrays.asList(429, 503);
        private final int maxRetries;

        public CustomHttpRequestRetryStrategy(int maxRetries, TimeValue defaultRetryInterval) {
            super(maxRetries, defaultRetryInterval, nonRetriableIOExceptionClasses, retriableCodes);
            this.maxRetries = maxRetries;
        }
    }

    public static class HttpUtilsBuilder {

        private final HttpClientBuilder httpClientBuilder;

        public HttpUtilsBuilder() {
            RequestConfig requestConfig = RequestConfig.custom()
                                                       .setConnectionRequestTimeout(30, TimeUnit.SECONDS)
                                                       .setResponseTimeout(30, TimeUnit.SECONDS)
                                                       .build();
            this.httpClientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig);
        }

        public HttpUtilsBuilder(int connectTimeout, int readTimeout) {
            RequestConfig requestConfig = RequestConfig.custom()
                                                       .setConnectionRequestTimeout(connectTimeout, TimeUnit.SECONDS)
                                                       .setResponseTimeout(readTimeout, TimeUnit.SECONDS)
                                                       .build();
            this.httpClientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig);
        }

        public HttpUtilsBuilder setRetry(int retryTimes, int duration) {
            if (retryTimes > 1 && duration > 0) {
                this.httpClientBuilder.setRetryStrategy(new CustomHttpRequestRetryStrategy(retryTimes, TimeValue.of(duration, TimeUnit.SECONDS)));
            }
            return this;
        }

        public HttpUtilsBuilder setProxy(String protocol, String host, int port) {
            HttpHost proxy = new HttpHost(protocol, host, port);
            this.httpClientBuilder.setProxy(proxy);
            return this;
        }

        public HttpUtils build() {
            return new HttpUtils(this.httpClientBuilder.build());
        }
    }
}
