package com.alpha.omega.cache.expiration.map;

import com.alpha.omega.cache.FalseNoOpFuture;
import com.alpha.omega.cache.NameableThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

public class CountdownExpiringMapEntry<K,V> implements ExpiringMapEntry<K> {

    private static final Logger logger = LoggerFactory.getLogger(CountdownExpiringMapEntry.class);

    private Map<K,V> expiringMap;

    private  ScheduledExecutorService executor ;

	private CountDownLatch countDownLatch;
	public static final String THREAD_FACTORY_NAME = "countdown.exp.map.entry";
    public static final Future<Boolean> NO_OP = new FalseNoOpFuture();

    public CountdownExpiringMapEntry(Map<K,V> expiringMap, CountDownLatch countDownLatch) {
		this.expiringMap = Collections.unmodifiableMap(expiringMap);
		this.executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(),
				new NameableThreadFactory(THREAD_FACTORY_NAME, Runtime.getRuntime().availableProcessors()));
		this.countDownLatch = countDownLatch;
    }

	@Override
	public Future<Boolean> expireMapEntry(K key, long expiresInMilliSeconds) {
		logger.info("CountdownExpiringMapEntry.Scheduling Evict call for  key {}  from {} MilliSeconds will expire at {}",
				new Object[]{key,expiresInMilliSeconds, new Date(System.currentTimeMillis() + expiresInMilliSeconds)});

		Future<Boolean> results = (executor != null)?executor.schedule(() -> {
			Boolean completed = Boolean.FALSE;
			try{
				logger.info("CountdownExpiringMapEntry.Evicting key {}  after of {}",key,expiresInMilliSeconds);
				expiringMap.put(key, null);
				logger.info("CountdownExpiringMapEntry.Evicted key {} successfully ",key);
				completed = Boolean.TRUE;
			} catch (Exception e){
				logger.info("CountdownExpiringMapEntry.Unable to remove from map key => {}", key);
			}
			countDownLatch.countDown();
			return completed;
		}, expiresInMilliSeconds, TimeUnit.MILLISECONDS ) :NO_OP;

		return results;
	}
}
