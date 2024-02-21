package com.alpha.omega.cache.concurrency;

import java.util.Objects;
import java.util.concurrent.Callable;

public class CacheKeyCallable<V> implements Callable<V> {

    String cacheKey;
    Callable<V> delegate;

    public CacheKeyCallable(String cacheKey, Callable<V> delegate) {
        this.cacheKey = cacheKey;
        this.delegate = delegate;
    }

    @Override
    public V call() throws Exception {
        return delegate.call();
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public Callable<V> getDelegate() {
        return delegate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheKeyCallable<?> that = (CacheKeyCallable<?>) o;
        return cacheKey.equals(that.cacheKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cacheKey);
    }
}
