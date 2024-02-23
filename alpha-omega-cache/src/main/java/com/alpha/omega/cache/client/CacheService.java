package com.alpha.omega.cache.client;

import java.util.List;
import java.util.Optional;

public interface CacheService {

    List<CacheDto> processCacheRequests(List<CacheDto> cacheDtoList);
    Optional<CacheDto> processCacheRequest(CacheDto cacheDto);
}
