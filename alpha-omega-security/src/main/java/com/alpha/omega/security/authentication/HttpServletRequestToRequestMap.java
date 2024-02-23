package com.alpha.omega.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.alpha.omega.core.Constants.*;

public class HttpServletRequestToRequestMap implements RequestToRequestMap<HttpServletRequest>{
	@Override
	public Map<String, Object> convertToMap(HttpServletRequest request) {
		Map<String, Object> requestMap = new ConcurrentHashMap<>();
		putIfNotNull(requestMap, PRINCIPAL, request.getHeader(PRINCIPAL));
		putIfNotNull(requestMap, IDENTITY_PROVIDER, request.getHeader(IDENTITY_PROVIDER));
		putIfNotNull(requestMap, REFRESH_TOKEN_HEADER, request.getHeader(REFRESH_TOKEN_HEADER));
		putIfNotNull(requestMap, CORRELATION_ID, request.getHeader(CORRELATION_ID));
		putIfNotNull(requestMap, CONTEXT_ID, request.getHeader(CONTEXT_ID));
		putIfNotNull(requestMap, HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION));

		if (!requestMap.containsKey(CORRELATION_ID)){
			putIfNotNull(requestMap, CORRELATION_ID, request.getAttribute(CORRELATION_ID));
		}

		return requestMap;
	}


	@Override
	public Map<String, Object> apply(HttpServletRequest httpServletRequest) {
		return convertToMap(httpServletRequest);
	}
}
