package com.alpha.omega.cache.config;

import com.alpha.omega.cache.CacheDao;
import com.alpha.omega.cache.CacheManagerCacheDAOImpl;
import com.alpha.omega.cache.NameableThreadFactory;
import com.alpha.omega.cache.client.CacheConstants;
import com.alpha.omega.cache.expiration.DefaultExpiringCacheEntry;
import com.alpha.omega.cache.expiration.ExpiringCacheEntry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;

public class BaseCacheConfiguration {

	@Bean("aoExpiringCacheEntry")
	@ConditionalOnMissingBean(name = "aoExpiringCacheEntry")
	ExpiringCacheEntry expiringCacheEntry(@Qualifier("aoCacheManager") CacheManager cacheManager,
										  CacheConfigProperties cacheConfigProperties){
		return new DefaultExpiringCacheEntry(cacheManager, new NameableThreadFactory(CacheConstants.LOCAL_THREAD_FACTORY_NAME));
	}

	@Bean("aoCacheDao")
	CacheDao cacheDao(@Qualifier("aoCacheManager") CacheManager cacheManager,
					  @Qualifier("aoExpiringCacheEntry")  ExpiringCacheEntry expiringCacheEntry,
					  CacheConfigProperties cacheConfigProperties){
		return new CacheManagerCacheDAOImpl(cacheManager, expiringCacheEntry);
	}
}
