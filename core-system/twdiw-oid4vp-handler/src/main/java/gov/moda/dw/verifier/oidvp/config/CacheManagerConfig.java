package gov.moda.dw.verifier.oidvp.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@EnableCaching
@Configuration
public class CacheManagerConfig {

    public static final String METADATA_CACHE_MANAGER = "metadata-CacheManager";
    public static final String DID_CACHE_MANAGER = "did-CacheManager";

    public static final String METADATA_CACHE_NAME = "metadata";
    public static final String DID_CACHE_NAME = "did";

    @Bean(name = METADATA_CACHE_MANAGER)
    public CacheManager metadataCaffeineCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(METADATA_CACHE_NAME);
        Caffeine<Object, Object> metadataCaffeine = Caffeine.newBuilder()
                                                            .expireAfterAccess(60, TimeUnit.MINUTES)
                                                            .maximumSize(5)
                                                            .initialCapacity(5);
        caffeineCacheManager.setCaffeine(metadataCaffeine);
        return caffeineCacheManager;
    }

    @Bean(name = DID_CACHE_MANAGER)
    @Primary
    public CacheManager didCaffeineCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(DID_CACHE_NAME);
        Caffeine<Object, Object> didInfoCaffeine = Caffeine.newBuilder()
                                                           .expireAfterAccess(60, TimeUnit.MINUTES)
                                                           .maximumSize(5)
                                                           .initialCapacity(5);
        caffeineCacheManager.setCaffeine(didInfoCaffeine);
        return caffeineCacheManager;
    }
}
