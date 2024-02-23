package com.alpha.omega.cache.utils;

import com.alpha.omega.cache.CacheDao;
import com.alpha.omega.cache.CacheManagerCacheDAOImpl;
import com.alpha.omega.cache.NameableThreadFactory;
import com.alpha.omega.cache.expiration.DefaultExpiringCacheEntry;
import com.alpha.omega.cache.expiration.ExpiringCacheEntry;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.SimpleCacheManager;

import static com.alpha.omega.cache.client.CacheConstants.LOCAL_THREAD_FACTORY_NAME;

public class CacheUtils {

	private static ExpiringCacheEntry expiringCacheEntry(CacheManager cacheManager){
		return new DefaultExpiringCacheEntry(cacheManager, new NameableThreadFactory(LOCAL_THREAD_FACTORY_NAME));
	}


	private static CacheManager cacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager();
		cacheManager.setCaffeine(Caffeine.newBuilder()
				.weakKeys()
				.recordStats());
		return cacheManager;
	}

	private static CacheManager simpleCacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		return cacheManager;
	}


	private static CacheManager concurrentMapCacheManager() {
		ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
		return cacheManager;
	}

	public static CacheDao defaultLocalCacheDao(){
		//CacheManager cacheManager = cacheManager();
		//CacheManager cacheManager = simpleCacheManager();
		CacheManager cacheManager = concurrentMapCacheManager();
		ExpiringCacheEntry expiringCacheEntry = expiringCacheEntry(cacheManager);
		return new CacheManagerCacheDAOImpl(cacheManager, expiringCacheEntry);
	}
}
