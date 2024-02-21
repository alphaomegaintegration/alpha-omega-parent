package com.alpha.omega.cache.expiration.map;

import com.alpha.omega.cache.FalseNoOpFuture;
import com.alpha.omega.cache.NameableThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

public class DefaultExpiringMapEntry<K,V> implements ExpiringMapEntry<K> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultExpiringMapEntry.class);

    private Map<K,V> expiringMap;

    private  ScheduledExecutorService executor ;

	public static final String THREAD_FACTORY_NAME = "exp.map.entry";
    public static final Future<Boolean> NO_OP = new FalseNoOpFuture();
    public DefaultExpiringMapEntry(Map<K,V> expiringMap, ThreadFactory threadFactory) {
        this.expiringMap = expiringMap;
		this.executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);
    }

    public DefaultExpiringMapEntry(Map<K,V>  expiringMap, ScheduledExecutorService executor) {
		this.expiringMap = expiringMap;
        this.executor = executor;
    }

    public DefaultExpiringMapEntry(Map<K,V> expiringMap) {
		this.expiringMap = expiringMap;
		this.executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(),
				new NameableThreadFactory(THREAD_FACTORY_NAME, Runtime.getRuntime().availableProcessors()));
    }

	@Override
	public Future<Boolean> expireMapEntry(K key, long expiresInMilliSeconds) {
		logger.debug("DefaultExpiringCacheEntry.Scheduling Evict call for  key {}  from {} MilliSeconds will expire at {}",
				new Object[]{key,expiresInMilliSeconds, new Date(System.currentTimeMillis() + expiresInMilliSeconds)});

		Future<Boolean> results = (executor != null)?executor.schedule(() -> {
			Boolean completed = Boolean.FALSE;
			try{
				logger.debug("DefaultExpiringCacheEntry.Evicting key {}  after of {}",key,expiresInMilliSeconds);
				expiringMap.put(key, null);
				logger.debug("DefaultExpiringCacheEntry.Evicted key {} successfully ",key);
				completed = Boolean.TRUE;
			} catch (Exception e){
				logger.warn("DefaultExpiringCacheEntry.Unable to remove from map key => {}", key);
			}
			return completed;
		}, expiresInMilliSeconds, TimeUnit.MILLISECONDS ) :NO_OP;

		return results;
	}
}
