package gov.moda.dw.issuer.oidvci.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
	
	@Value("${chrisky.proxy.use}")
    private boolean useProxy;

    @Value("${chrisky.proxy.host}")
    private String proxyHost;

    @Value("${chrisky.proxy.port}")
    private int proxyPort;
    
    @Value("${chrisky.ssl}")
    private boolean useSsl;

	@Bean
    public RestTemplate restTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		if (useProxy) {
			// 設置代理的主機和端口
	        HttpHost proxy = new HttpHost("10.160.3.88", 8080);
			
	        // 使用代理設置 HttpClient
	        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
	        
	        // 設置超時配置
	        RequestConfig config = RequestConfig.custom()
	            .setResponseTimeout(Timeout.ofSeconds(10)) // 設置讀取超時 10 秒
	            .setConnectionRequestTimeout(Timeout.ofSeconds(3)) // 設置請求超時 3 秒
	            .build();
	        
	        // 設置忽略 SSL 驗證
	        SSLContext sslContext = SSLContextBuilder.create()
	                .loadTrustMaterial((chain, authType) -> true) // 忽略證書驗證
	                .build();

	        // 創建忽略 SSL 驗證的連接工廠
	        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
	                sslContext,
	                NoopHostnameVerifier.INSTANCE // 忽略主機名驗證
	        );
	        
	        HttpClientConnectionManager conncetManager =
	                PoolingHttpClientConnectionManagerBuilder.create()
	                    .setSSLSocketFactory(socketFactory)
	                    .build();
	        
	        CloseableHttpClient httpClient;
	        
	        if(useSsl)
	        {
		        httpClient = HttpClients.custom()
			            .setRoutePlanner(routePlanner)
			            .setDefaultRequestConfig(config)
			            .build();
	        }
	        else
	        {
	        	httpClient = HttpClients.custom()
	    	            .setRoutePlanner(routePlanner)
	    	            .setDefaultRequestConfig(config)
	    	            .setConnectionManager(conncetManager)
	    	            .build();
	        }
	
	        // 使用 HttpClient 創建 RestTemplate
	        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
	        
	        return new RestTemplate(factory);
		}
		else
		{
			// 設置超時配置
	        RequestConfig config = RequestConfig.custom()
	            .setResponseTimeout(Timeout.ofSeconds(10)) // 設置讀取超時 10 秒
	            .setConnectionRequestTimeout(Timeout.ofSeconds(3)) // 設置請求超時 3 秒
	            .build();
	        
	        // 設置忽略 SSL 驗證
	        SSLContext sslContext = SSLContextBuilder.create()
	                .loadTrustMaterial((chain, authType) -> true) // 忽略證書驗證
	                .build();

	        // 創建忽略 SSL 驗證的連接工廠
	        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
	                sslContext,
	                NoopHostnameVerifier.INSTANCE // 忽略主機名驗證
	        );
	        
	        HttpClientConnectionManager conncetManager =
	                PoolingHttpClientConnectionManagerBuilder.create()
	                    .setSSLSocketFactory(socketFactory)
	                    .build();
	        
	        CloseableHttpClient httpClient;
	        
	        if(useSsl)
	        {
		        httpClient = HttpClients.custom()
		                .setDefaultRequestConfig(config)
		                .build();
	        }
	        else
	        {
	        	httpClient = HttpClients.custom()
		                .setDefaultRequestConfig(config)
		                .setConnectionManager(conncetManager)
		                .build();
	        }
	
	        // 使用 HttpClient 創建 RestTemplate
	        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
	        
	        return new RestTemplate(factory);
		}
    }
}
