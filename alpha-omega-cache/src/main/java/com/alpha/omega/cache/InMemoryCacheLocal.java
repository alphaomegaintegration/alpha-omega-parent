package com.alpha.omega.cache;

import com.alpha.omega.cache.expiration.Expiration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryCacheLocal implements CacheDao {

    private static Logger logger = LoggerFactory.getLogger(InMemoryCacheLocal.class);

    private static final int DEFAULT_EXPIRATION_SECONDS = 86400;
    private static final Expiration DEFUAULT_EXPIRATION = Expiration.byDeltaSeconds(DEFAULT_EXPIRATION_SECONDS);
    private Map<String, Long> expiresMap = new HashMap<>();
    private Map<String, Map<String, byte[]>> namespaceMap = new HashMap<>();

    private static ObjectMapper objectMapper = new DefaultObjectMapperFactory().createObjectMapper(ObjectMapperFactory.Scope.PROTOTYPE);

    private static final String IMPLEMENTATION_TYPE = "MockCacheLocal2";
    private static final String CHAR_ENCODING = "UTF-8";

    private Map<String, byte[]> getNamespaceMap(String namespace) {
        namespaceMap.computeIfAbsent(namespace, k -> new HashMap<>());
        return namespaceMap.get(namespace);

    }

    @Override
    public String getImplementationType(){
        return IMPLEMENTATION_TYPE;
    }

    @Override
    public <T> T getObjectFromCache(String namespace, String sessionId, Class<T> clazz) {
        logger.info("getObjectFromCache (mock) namespace["+namespace+", key["+sessionId+"], class["+clazz.getName()+"]");
        Long expiration = expiresMap.get(sessionId);
        if (expiration != null && System.currentTimeMillis() > expiration) {
            removeFromCache(namespace, sessionId);
        } else {
            if (getNamespaceMap(namespace).get(sessionId) != null) {
                try {
                	String objectAsString = new String(getNamespaceMap(namespace).get(sessionId), CHAR_ENCODING);
                    return objectMapper.readValue(objectAsString, clazz);
                } catch (IOException e) {
                    logger.error("Error parsing cache entry! " + e.getMessage());
                    removeFromCache(namespace, sessionId);
                    return null;
                }
            }
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
    public void putInCache(String namespace, String key, Object value, Expiration expiresInSeconds) {
        try {
            getNamespaceMap(namespace).put(key, objectMapper.writeValueAsString(value).getBytes(CHAR_ENCODING));
        } catch (JsonProcessingException | UnsupportedEncodingException e ) {
            logger.error(e.getMessage());
        }
        expiresMap.put(key, expiresInSeconds.getMillisecondsValue());
    }

    @Override
    public void putInCache(String namespace, String key, Object value) {
        putInCache(namespace, key, value, DEFUAULT_EXPIRATION);
    }

    @Override
    public void removeFromCache(String namespace, String key) {
        expiresMap.remove(key);
        getNamespaceMap(namespace).remove(key);
    }

}
