package tz.co.flex.payment.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Cache configuration for the application.
 * Uses Ehcache as the JCache provider for Bucket 4j rate limiting.
 * Configuration is defined in ehcache.xml and application.properties.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public javax.cache.CacheManager jCacheManager() throws URISyntaxException {
        // Explicitly specify Ehcache provider to avoid conflicts with Caffeine JCache
        CachingProvider cachingProvider = Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
        URL ehcacheConfigUrl = getClass().getResource("/ehcache.xml");
        if (ehcacheConfigUrl == null) {
            throw new IllegalStateException("ehcache.xml configuration file not found on classpath");
        }
        return cachingProvider.getCacheManager(
                ehcacheConfigUrl.toURI(),
                getClass().getClassLoader()
        );
    }

    @Bean
    @Primary
    public CacheManager cacheManager(javax.cache.CacheManager jCacheManager) {
        return new JCacheCacheManager(jCacheManager);
    }
}
