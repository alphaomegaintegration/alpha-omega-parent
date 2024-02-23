package com.alpha.omega.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
@EnableConfigurationProperties(CacheConfigProperties.class)
@ConditionalOnProperty(prefix = "cache.provider", name = "name", havingValue = "local",matchIfMissing = true)
public class DefaultLocalCacheConfiguration extends BaseCacheConfiguration{

	private static final Logger logger = LoggerFactory.getLogger(DefaultLocalCacheConfiguration.class);

	@Autowired
	CacheConfigProperties cacheConfigProperties;

	public void setCacheConfigProperties(CacheConfigProperties cacheConfigProperties) {
		this.cacheConfigProperties = cacheConfigProperties;
	}

	@Bean("aoCacheManager")
	@ConditionalOnProperty(prefix = "cache.provider", name = "name", havingValue = "local",matchIfMissing = true)
	@ConditionalOnMissingBean(name = "aoCacheManager")
	public CacheManager cacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager();
		cacheManager.setCaffeine(Caffeine.newBuilder()
				.recordStats());
		return cacheManager;
	}

	@PostConstruct
	public void postInit(){
		logger.info("Configured => {} with properties  => {}", this.getClass().getName(), cacheConfigProperties);
	}

}
