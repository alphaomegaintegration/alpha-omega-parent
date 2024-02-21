package com.alpha.omega.security.rx;

import com.enterprise.pwc.datalabs.security.authentication.RequestToRequestMap;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.pwc.base.utils.BaseConstants.*;

public class AORxServerWebExchangeToRequestMap implements RequestToRequestMap<ServerWebExchange> {
	@Override
	public Map<String, Object> convertToMap(ServerWebExchange serverWebExchange) {

		HttpHeaders headers = serverWebExchange.getRequest().getHeaders();

		Map<String, Object> requestMap = new ConcurrentHashMap<>();
		putIfNotNull(requestMap, PRINCIPAL, headers.getFirst(PRINCIPAL));
		putIfNotNull(requestMap, IDENTITY_PROVIDER, headers.getFirst(IDENTITY_PROVIDER));
		putIfNotNull(requestMap, REFRESH_TOKEN_HEADER, headers.getFirst(REFRESH_TOKEN_HEADER));
		putIfNotNull(requestMap, ENGAGEMENT_ID, headers.getFirst(ENGAGEMENT_ID));
		putIfNotNull(requestMap, CORRELATION_ID, headers.getFirst(CORRELATION_ID));
		putIfNotNull(requestMap, CONTEXT_ID, headers.getFirst(CONTEXT_ID));
		putIfNotNull(requestMap, HttpHeaders.AUTHORIZATION, headers.getFirst(HttpHeaders.AUTHORIZATION));
		return requestMap;
	}

	@Override
	public Map<String, Object> apply(ServerWebExchange serverWebExchange) {
		return convertToMap(serverWebExchange);
	}
}
