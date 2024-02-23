package com.alpha.omega.cache;

import com.alpha.omega.cache.expiration.Expiration;
import com.alpha.omega.cache.expiration.ExpiresInSeconds;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public interface CacheDao {

	<T> T getObjectFromCache(String namespace, String cacheKey, Class<T> clazz);

	<T> Optional<T> getObjectFromCacheOptional(String namespace, String sessionId, Class<T> clazz);

	void logMessage(String logMessage);

	void putInCache(String namespace, String key, Object value, Expiration expiresInSeconds);

	void putInCache(String namespace, String key, Object value);

	void removeFromCache(String namespace, String key);

	String getImplementationType();

	default <T> T get(CacheableMetadata<T> cacheableMetadata, Supplier<T> tSupplier) {
		return get(cacheableMetadata.getClazz(), cacheableMetadata.getNamespace(), cacheableMetadata.getKey(), tSupplier, (T result) -> cacheableMetadata.getExpiration());
	}

	default <T> Optional<T> getOptional(CacheableMetadata<T> cacheableMetadata, Supplier<Optional<T>> optionalSupplier) {
		return getOptional(cacheableMetadata.getClazz(), cacheableMetadata.getNamespace(), cacheableMetadata.getKey(), optionalSupplier, (Optional<T> result) -> cacheableMetadata.getExpiration());
	}

	default <T extends ExpiresInSeconds> T get(Class<T> clazz, String namespace, String key, Supplier<T> function) {
		return get(clazz, namespace, key, function, (T result) -> Expiration.byDeltaSeconds(result.getExpiresInSecond().intValue()));
	}

	default <T> T get(Class<T> clazz, String namespace, String key, Supplier<T> function, Function<T, Expiration> expiresFunction) {
		logMessage("looking in cache for " + namespace + " " + key);
		T val = getObjectFromCache(namespace, key, clazz);
		if (val == null) {
			logMessage("did not find for key:" + key + ", namespace:" + namespace);
			val = function.get();
			if (val != null) {
				logMessage("putting in cache (" + "NAMESPACE: " + namespace + " / KEY: " + key + "): " + StringUtils.left(val.toString(), 100));
				putInCache(namespace, key, val, expiresFunction.apply(val));
			} else {
				logMessage("Nothing returned from function.  Putting nothing in cache for (" + "NAMESPACE:" + namespace + " / KEY: " + key + ")");
			}
		}
		return val;
	}

	default <T> Optional<T> getOptional(Class<T> clazz, String namespace, String key, Supplier<Optional<T>> function, Function<Optional<T>, Expiration> expiresFunction) {
		logMessage("looking in cache for " + namespace + " " + key);
		Optional<T> val = Optional.ofNullable(getObjectFromCache(namespace, key, clazz));
		if (!val.isPresent()) {
			logMessage("did not find for key:" + key + ", namespace:" + namespace);
			val = function.get();
			if (val.isPresent()) {
				logMessage("putting in cache (" + "NAMESPACE: " + namespace + " / KEY: " + key + "): " + StringUtils.left(val.toString(), 100));

				putInCache(namespace, key, val, expiresFunction.apply(val));
			} else {
				logMessage("Nothing returned from function.  Putting nothing in cache for (" + "NAMESPACE:" + namespace + " / KEY: " + key + ")");
			}
		}
		return val;
	}

	default <T> T get(CacheableMetadata<T> cacheableMetadata, Supplier<T> dataFunction, CacheControl cacheState) {
		switch (cacheState) {
			case REFRESH:
				removeFromCache(cacheableMetadata.getNamespace(), cacheableMetadata.getKey());
				T result = dataFunction.get();
				putInCache(cacheableMetadata.getNamespace(), cacheableMetadata.getKey(), result, cacheableMetadata.getExpiration());
				return result;
			case NONE:
				return dataFunction.get();
			case USE:
			default:
				return get(cacheableMetadata, dataFunction);
		}
	}

	default <T> Mono<T> getMono(final CacheableMetadata<T> cacheableMetadata,final Mono<Supplier<T>> dataFunction) {
		return Mono.from(dataFunction)
				.publishOn(Schedulers.boundedElastic())
				.map(dFunc -> get(cacheableMetadata, dFunc, cacheableMetadata.getCacheControl()));
	}

	default <T> Optional<T> getOptional(CacheableMetadata<T> cacheableMetadata, Supplier<Optional<T>> dataFunction, CacheControl cacheState) {
		Optional<T> optionalResult;
		switch (cacheState) {
			case REFRESH:
				removeFromCache(cacheableMetadata.getNamespace(), cacheableMetadata.getKey());
				optionalResult = dataFunction.get();
				break;
			case NONE:
				optionalResult = dataFunction.get();
				break;
			case USE:
			default:
				optionalResult = getOptional(cacheableMetadata, dataFunction);
		}
		optionalResult.ifPresent(
				result -> putInCache(cacheableMetadata.getNamespace(), cacheableMetadata.getKey(), result, cacheableMetadata.getExpiration()));
		return optionalResult;
	}

	default <T> List<T> getList(CacheableMetadata<T[]> cacheableMetadata, Supplier<List<T>> dataFunction, CacheControl cacheState) {
		switch (cacheState) {
			case REFRESH:
				removeFromCache(cacheableMetadata.getNamespace(), cacheableMetadata.getKey());
				List<T> result = dataFunction.get();
				putInCache(cacheableMetadata.getNamespace(), cacheableMetadata.getKey(), result, cacheableMetadata.getExpiration());
				return result;
			case NONE:
				return dataFunction.get();
			case USE:
			default:
				return getList(cacheableMetadata, dataFunction);
		}
	}

	default <T> List<T> getList(CacheableMetadata<T[]> cacheableMetadata, Supplier<List<T>> listSupplier) {
		logMessage("looking in cache for " + cacheableMetadata + " using implementation: " + getImplementationType());
		T[] val = getObjectFromCache(cacheableMetadata.getNamespace(), cacheableMetadata.getKey(), cacheableMetadata.getClazz());
		logMessage("val = " + val);
		if (val == null) {
			logMessage("did not find for key : " + cacheableMetadata.getKey());
			List<T> valueList = listSupplier.get();
			logMessage("putting in cache (" + "NAMESPACE: " + cacheableMetadata.getNamespace() + " / KEY:" + cacheableMetadata.getKey() + "): " + StringUtils.left(valueList.toString(), 100));
			putInCache(cacheableMetadata.getNamespace(), cacheableMetadata.getKey(), valueList, cacheableMetadata.getExpiration());
			return valueList;
		}
		logMessage("returning  " + val);
		return Arrays.asList(val);
	}

	default <T, R> List<R> getList(String namespace, Class<R> clazz, Expiration expiration, Function<List<T>, Map<String, R>> dataFunction, Map<String, T> inputKeyToClass, CacheControl cacheState) {
		Map<String, R> cachedValuesByKey = new HashMap<>();

		inputKeyToClass.forEach((key, value) ->
				Optional.ofNullable(getObjectFromCache(namespace, key, clazz))
						.ifPresent(cachedObject -> cachedValuesByKey.put(key, cachedObject))
		);

		List<T> notFoundInCacheList = inputKeyToClass.keySet().stream().filter(key ->
				cachedValuesByKey.get(key) == null
		).map(inputKeyToClass::get).collect(Collectors.toList());

		if (!notFoundInCacheList.isEmpty()) {
			Map<String, R> externalValuesByKey = dataFunction.apply(notFoundInCacheList);

			externalValuesByKey.forEach((key, result) -> putInCache(namespace, key, result, expiration));

			cachedValuesByKey.putAll(externalValuesByKey);
		}

		return new ArrayList<>(cachedValuesByKey.values());

	}



}
