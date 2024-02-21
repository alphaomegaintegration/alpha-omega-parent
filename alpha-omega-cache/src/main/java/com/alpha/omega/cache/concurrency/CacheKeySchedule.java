package com.alpha.omega.cache.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface CacheKeySchedule {
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit, String cacheKey);
}
