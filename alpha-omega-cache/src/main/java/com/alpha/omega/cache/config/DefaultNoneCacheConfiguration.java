package com.alpha.omega.cache.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(CacheConfigProperties.class)
@ConditionalOnProperty(prefix = "cache.provider", name = "name", havingValue = "none",matchIfMissing = false)
public class DefaultNoneCacheConfiguration extends BaseCacheConfiguration{

	private static final Logger logger = LoggerFactory.getLogger(DefaultNoneCacheConfiguration.class);

	@Autowired
	CacheConfigProperties cacheConfigProperties;

	public void setCacheConfigProperties(CacheConfigProperties cacheConfigProperties) {
		this.cacheConfigProperties = cacheConfigProperties;
	}

	@Bean("aoCacheManager")
	@ConditionalOnProperty(prefix = "cache.provider", name = "name", havingValue = "none",matchIfMissing = false)
	@ConditionalOnMissingBean(name = "aoCacheManager")
	public CacheManager cacheManager() {
		return new NoOpCacheManager();
	}

	@PostConstruct
	public void postInit(){
		logger.info("Configured => {} with properties  => {}", this.getClass().getName(), cacheConfigProperties);
	}


}
