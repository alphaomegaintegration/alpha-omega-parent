package com.alpha.omega.security.authentication;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public interface RequestToRequestMap<T> extends Function<T, Map<String, Object>>{

	public Map<String, Object> convertToMap(T t);

	default Function<T, Map<String, Object>> convert(){
		return (t) ->convertToMap(t);
	}

	default void putIfNotNull(Map<String,Object> requestMap, String key,Object value){
		if (Objects.nonNull(value)){
			requestMap.put(key, value);
		}
	}

}
