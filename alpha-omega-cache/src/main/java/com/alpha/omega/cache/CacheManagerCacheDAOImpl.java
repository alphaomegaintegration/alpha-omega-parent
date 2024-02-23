package com.alpha.omega.cache;

import com.alpha.omega.cache.expiration.DefaultExpiringCacheEntry;
import com.alpha.omega.cache.expiration.Expiration;
import com.alpha.omega.cache.expiration.ExpiringCacheEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;

public class CacheManagerCacheDAOImpl implements CacheDao{


    private static final Logger logger = LoggerFactory.getLogger(CacheManagerCacheDAOImpl.class);
    private static final String LOG_MSG_CACHED_OBJECT_NOT_FOUND_IN_NAMESPACE = "cached object [%s] not found in [%s] namespace because [%s";
    private static final String LOG_MSG_ERR_EXCEPTION_PARSING_OBJECT_FROM_CACHE = "Exception parsing object from cache: [%s, %s]";
    private static final String LOG_MSG_ERR_EXCEPTION_PUTTING_INCACHE = "Exception putting object in cache: %s";
    private static final String LOG_MSG_GETTING_OBJECT_FROM_NAMESPACE_OF_TYPE_S = "getting object %s from namespace (%s) of type: %s";
    private static final String LOG_MSG_PUT_IN_CACHE_WITHOUT_EXPIRATION = "putting [%s, %s] in %s namespace with no expiration";
    private static final String LOG_MSG_PUT_IN_CACHE_WITH_EXPIRATION = "putting [%s, %s] in %s namespace to expire in %s seconds";

    public static final long DEFAULT_EXPIRY = 10000;

    private ThreadFactory threadFactory;

    private static final String IMPLEMENTATION_TYPE = "CacheManager";

    ObjectMapperFactory objectMapperFactory = new DefaultObjectMapperFactory();

    ExpiringCacheEntry expiringCacheEntry;

    ObjectMapperFactory.Scope objectMapperScope = (StringUtils.isNotBlank(System.getProperty("cache.objectmapper.scope")))
            ? ObjectMapperFactory.Scope.valueOf(System.getProperty("cache.objectmapper.scope"))
            : ObjectMapperFactory.Scope.SINGLETON;

    @Override
    public String getImplementationType() {
        return IMPLEMENTATION_TYPE;
    }

    CacheManager cacheManager;

    public CacheManagerCacheDAOImpl(ObjectMapperFactory objectMapperFactory, ObjectMapperFactory.Scope objectMapperScope, CacheManager cacheManager) {
        this.objectMapperFactory = objectMapperFactory;
        this.objectMapperScope = objectMapperScope;
        this.cacheManager = cacheManager;
        this.expiringCacheEntry = new DefaultExpiringCacheEntry(this.cacheManager);
    }

    public CacheManagerCacheDAOImpl(ObjectMapperFactory objectMapperFactory, ObjectMapperFactory.Scope objectMapperScope,
                                    CacheManager cacheManager, ExpiringCacheEntry expiringCacheEntry) {
        this.objectMapperFactory = objectMapperFactory;
        this.objectMapperScope = objectMapperScope;
        this.cacheManager = cacheManager;
        this.expiringCacheEntry = expiringCacheEntry;
    }

    public CacheManagerCacheDAOImpl(ObjectMapperFactory objectMapperFactory, CacheManager cacheManager) {
        this.objectMapperFactory = objectMapperFactory;
        this.cacheManager = cacheManager;
        this.expiringCacheEntry = new DefaultExpiringCacheEntry(this.cacheManager);
    }

    public CacheManagerCacheDAOImpl(ObjectMapperFactory objectMapperFactory, CacheManager cacheManager, ExpiringCacheEntry expiringCacheEntry) {
        this.objectMapperFactory = objectMapperFactory;
        this.cacheManager = cacheManager;
        this.expiringCacheEntry = (expiringCacheEntry != null) ? expiringCacheEntry: new DefaultExpiringCacheEntry(this.cacheManager);
    }

    public CacheManagerCacheDAOImpl(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.expiringCacheEntry = new DefaultExpiringCacheEntry(this.cacheManager);
    }

    public CacheManagerCacheDAOImpl(CacheManager cacheManager, ThreadFactory threadFactory) {
        this.cacheManager = cacheManager;
        this.threadFactory = threadFactory;
        this.expiringCacheEntry = new DefaultExpiringCacheEntry(this.cacheManager,this.threadFactory);
    }

    public CacheManagerCacheDAOImpl(CacheManager cacheManager, ExpiringCacheEntry expiringCacheEntry) {
        this.cacheManager = cacheManager;
        this.expiringCacheEntry = (expiringCacheEntry != null) ? expiringCacheEntry: new DefaultExpiringCacheEntry(this.cacheManager);
    }


    @Override
    public <T> T getObjectFromCache(final String namespace, final String key, Class<T> clazz) {

        logger.trace("using Scope {}", objectMapperScope);
        logger.debug(String.format(LOG_MSG_GETTING_OBJECT_FROM_NAMESPACE_OF_TYPE_S, key, namespace, clazz.getName()));
        ObjectMapper objectMapper = objectMapperFactory.createObjectMapper(objectMapperScope);

		logger.trace("Trying to getObjectFromCache (CacheManagerCacheDAOImpl) namespace => [{}], key => [{}], class => {}", new Object[]{namespace, key, clazz.getName()});
		String value = null;
        try {
            value = (String) cacheManager.getCache(namespace).get(key).get();
            if (StringUtils.isNotBlank(value)) {
				logger.trace("found value => [{}] getObjectFromCache (CacheManagerCacheDAOImpl) namespace => [{}], key => [{}], class => {}", new Object[]{StringUtils.left(value, 100), namespace, key, clazz.getName()});
                return objectMapper.readValue(value, clazz);
            } else {
				logger.trace("Could not getObjectFromCache (CacheManagerCacheDAOImpl) namespace =>[{}], key => [{}], class => {}", new Object[]{namespace, key, clazz.getName()});
			}
        } catch (NullPointerException npe) {
			if (logger.isDebugEnabled()){
				npe.printStackTrace();
			}
            logger.warn(String.format(LOG_MSG_CACHED_OBJECT_NOT_FOUND_IN_NAMESPACE, key, namespace, npe.getMessage()));
        } catch (IOException e) {
			logger.error("{}",new StringBuilder(String.format(LOG_MSG_ERR_EXCEPTION_PARSING_OBJECT_FROM_CACHE, key, namespace))
					.append("IOException ")
					.append(e.toString())
					.append(" tried to parse ")
					.append(value)
					.append(" as: ")
					.append(clazz.getName()).toString());
        }
        return null;
    }

	@Override
	public <T> Optional<T> getObjectFromCacheOptional(String namespace, String sessionId, Class<T> clazz) {
		return Optional.ofNullable(this.getObjectFromCache(namespace, sessionId, clazz));
	}

	@Override
    public void logMessage(String logMessage) {
        logger.info(logMessage);
    }

    @Override
    public void putInCache(String namespace, String key, Object value) {

        ObjectMapper objectMapper = objectMapperFactory.createObjectMapper(objectMapperScope);
        logger.trace(String.format(LOG_MSG_PUT_IN_CACHE_WITHOUT_EXPIRATION, key, value.toString(), namespace));
        try {
            cacheManager.getCache(namespace).put(key, objectMapper.writeValueAsString(value));
        } catch (Exception e) {
            logger.warn(String.format(LOG_MSG_ERR_EXCEPTION_PUTTING_INCACHE, e.getMessage()));
        }
    }

    @Override
    public void putInCache(String namespace, String key, Object value, Expiration expiresInSeconds) {

        long dif = expiresInSeconds.getMillisecondsValue() - System.currentTimeMillis();
        long expiry = (dif > 0) ? dif : DEFAULT_EXPIRY;

        logger.trace("putInCache Expiry => {} dif => {} now => {}",expiry, dif, Instant.now());
        this.putInCache(namespace, key, value);
        this.expiringCacheEntry.expireCacheEntry(namespace, key, dif);

    }

    @Override
    public void removeFromCache(String namespace, String key) {
        logger.debug("Removing namespace => {} with key => {}",namespace,key);
        cacheManager.getCache(namespace).evict(key);
    }

}
