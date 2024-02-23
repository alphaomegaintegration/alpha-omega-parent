package com.alpha.omega.cache.instant;

import java.io.Serializable;
import java.time.Instant;

public class DefaultCacheInstant implements CacheInstant, Serializable {
    Instant instant;

    @Override
    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }
}
