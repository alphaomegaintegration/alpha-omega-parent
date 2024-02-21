package com.alpha.omega.cache.instant;

import java.io.Serializable;
import java.time.Instant;

public interface CacheInstant extends Serializable {
    Instant getInstant();
}
