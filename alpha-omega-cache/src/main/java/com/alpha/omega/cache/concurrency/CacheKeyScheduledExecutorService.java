package com.alpha.omega.cache.concurrency;

import java.util.concurrent.*;

public class CacheKeyScheduledExecutorService extends ScheduledThreadPoolExecutor implements CacheKeySchedule{

    public CacheKeyScheduledExecutorService(int corePoolSize) {
        super(corePoolSize);
    }

    public CacheKeyScheduledExecutorService(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    public CacheKeyScheduledExecutorService(int corePoolSize, RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
    }

    public CacheKeyScheduledExecutorService(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }

    public CacheKeyScheduledExecutorService(ScheduledThreadPoolExecutor executor) {
        super(executor.getCorePoolSize(), executor.getThreadFactory(), executor.getRejectedExecutionHandler());
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit, String cacheKey) {
        return super.schedule(callable, delay, unit);
    }


}
