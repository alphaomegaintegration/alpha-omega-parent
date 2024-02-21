package com.alpha.omega.cache.expiration;

import com.alpha.omega.cache.DefaultObjectMapperFactory;
import com.alpha.omega.cache.ObjectMapperFactory;
import com.alpha.omega.cache.concurrency.CacheKeyCallable;
import com.alpha.omega.cache.concurrency.CacheKeyScheduledExecutorService;
import com.alpha.omega.cache.instant.DefaultCacheInstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CheckingExpiringCacheEntry implements ExpiringCacheEntry {
    private static final Logger logger = LoggerFactory.getLogger(CheckingExpiringCacheEntry.class);

    private CacheManager cacheManager;
    private ScheduledExecutorService executor;
    ObjectMapperFactory objectMapperFactory = new DefaultObjectMapperFactory();

    public CheckingExpiringCacheEntry(CacheManager cacheManager, ThreadFactory threadFactory) {
        this.cacheManager = cacheManager;
        this.executor = new CacheKeyScheduledExecutorService(Runtime.getRuntime().availableProcessors(), threadFactory);
    }

    public CheckingExpiringCacheEntry(CacheManager cacheManager, ScheduledThreadPoolExecutor executor) {
        this.cacheManager = cacheManager;
        this.executor = new CacheKeyScheduledExecutorService(executor);
    }


    public CheckingExpiringCacheEntry(CacheManager cacheManager, ScheduledExecutorService executor) {
        this.cacheManager = cacheManager;
        this.executor = executor;
    }


    @Override
    public Future<Boolean> expireCacheEntry(String namespace, String key, long expiresInMilliSeconds) {

        final Instant expireInstant = Instant.ofEpochMilli(new Date().getTime());

        logger.debug("CheckingExpiringCacheEntry.expireCacheEntry Scheduling Evict call for namespace {} cacheKey {} " +
                        "from {} MilliSeconds should expire at {} with expireInstant => {}",
                new Object[]{namespace, key, expiresInMilliSeconds,
                        new Date(System.currentTimeMillis() + expiresInMilliSeconds), expireInstant.toString()});

        // This will be run expiresInMilliSeconds from now. There could be multiple entries put in the cache with the same
        // key in between now and then. The expireInstant is the time this was called, which is the same time the value
        // was put in the cache. When this runs expiresInMilliSeconds from now, we will check the cache retrieved object's
        // Instant against the expireInstant. If the expireInstant is after the value's instant or equal that means the
        // value in the cache has already expired. Any entries with the same key put in AFTER this current worker will not
        // be evicted. There will be another associated worker with that same time to evict that value.
        CacheKeyCallable<Boolean> cacheKeyCallable = new CacheKeyCallable(key, () -> {
            Boolean completed = Boolean.FALSE;
            try {
                boolean shouldEvict = isShouldEvict(namespace, key, expireInstant);
                if (shouldEvict){
                    logger.debug("CheckingExpiringCacheEntry.Evicting key {} from namespace {} after of {}", key, namespace, expiresInMilliSeconds);
                    cacheManager.getCache(namespace).evict(key);
                    logger.debug("CheckingExpiringCacheEntry.Evicted key {} successfully ", key);
                    completed = Boolean.TRUE;
                }
            } catch (Exception e) {
                logger.warn("CheckingExpiringCacheEntry.Unable to remove from cache namespace => {} key => {}", namespace, key);
            }
            return completed;
        });

        Future<Boolean> results  = executor.schedule(cacheKeyCallable, expiresInMilliSeconds, TimeUnit.MILLISECONDS);
        return results;
    }

    boolean isShouldEvict(String namespace, String key, Instant expireInstant) {

        final AtomicBoolean shouldEvict = new AtomicBoolean(Boolean.TRUE);
        ObjectMapper objectMapper = objectMapperFactory.createObjectMapper( ObjectMapperFactory.Scope.SINGLETON);

        String value = null;
        try{
            value = cacheManager.getCache(namespace).get(key, String.class);
        } catch (IllegalStateException e){
            logger.debug("CheckingExpiringCacheEntry.isShouldEvict namespace {} cacheKey {}  Cannot cast into String => {}",
                    new Object[]{namespace, key,e.getMessage()});
        }

        DefaultCacheInstant instant = null;
        if (StringUtils.isNotBlank(value)){
            try {
                instant = objectMapper.readValue(value, DefaultCacheInstant.class);
            } catch (Exception e) {
                logger.debug("CheckingExpiringCacheEntry.isShouldEvict Cannot read json string {} into  DefaultCacheInstant ",value,e.getMessage());
            }
        }
        Optional<DefaultCacheInstant> cacheInstantOptional = Optional.ofNullable(instant);
        cacheInstantOptional.ifPresent(cacheInstant -> {
            boolean isAfter = expireInstant.isAfter(cacheInstant.getInstant()) | expireInstant.equals(cacheInstant.getInstant());
            logger.debug("CheckingExpiringCacheEntry.isShouldEvict namespace {} cacheKey {} expire instant => {} : {} <= cache expire Instant : expire instant is after or equal => {} ",
                    new Object[]{namespace, key,expireInstant.toString(),cacheInstant.getInstant().toString(), isAfter});
            shouldEvict.set(isAfter);

        });

        logger.debug("CheckingExpiringCacheEntry.isShouldEvict namespace {} cacheKey {} should eveict => {}",
                new Object[]{namespace, key, shouldEvict.get()});
        return shouldEvict.get();
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }
}
