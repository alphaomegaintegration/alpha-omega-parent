package com.alpha.omega.cache.expiration;

import com.alpha.omega.cache.FalseNoOpFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;

import java.util.Date;
import java.util.concurrent.*;

public class DefaultExpiringCacheEntry implements ExpiringCacheEntry {

    private static final Logger logger = LoggerFactory.getLogger(DefaultExpiringCacheEntry.class);

    private CacheManager cacheManager;

    private  ScheduledExecutorService executor ;

    public static final Future<Boolean> NO_OP = new FalseNoOpFuture();
    public DefaultExpiringCacheEntry(CacheManager cacheManager, ThreadFactory threadFactory) {
        this.cacheManager = cacheManager;
        this.executor = Executors.newSingleThreadScheduledExecutor(threadFactory);
    }

    public DefaultExpiringCacheEntry(CacheManager cacheManager, ScheduledExecutorService executor) {
        this.cacheManager = cacheManager;
        this.executor = executor;
    }

    public DefaultExpiringCacheEntry(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }


    @Override
    public Future<Boolean> expireCacheEntry(String namespace, String key, long expiresInMilliSeconds) {

        logger.debug("DefaultExpiringCacheEntry.Scheduling Evict call for  key {} from namespace {} from {} MilliSeconds will expire at {}",
                new Object[]{key,namespace,expiresInMilliSeconds, new Date(System.currentTimeMillis() + expiresInMilliSeconds)});

        Future<Boolean> results = (executor != null)?executor.schedule(() -> {
            Boolean completed = Boolean.FALSE;
            try{
                logger.debug("DefaultExpiringCacheEntry.Evicting key {} from namespace {} after of {}",key,namespace,expiresInMilliSeconds);
                cacheManager.getCache(namespace).evict(key);
                logger.debug("DefaultExpiringCacheEntry.Evicted key {} successfully ",key);
                completed = Boolean.TRUE;
            } catch (Exception e){
                logger.warn("DefaultExpiringCacheEntry.Unable to remove from cache namespace => {} key => {}",namespace, key);
            }
            return completed;
        }, expiresInMilliSeconds, TimeUnit.MILLISECONDS ) :NO_OP;

        return results;
    }

}
