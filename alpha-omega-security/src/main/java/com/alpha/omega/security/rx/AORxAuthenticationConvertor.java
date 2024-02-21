package com.alpha.omega.security.rx;

import com.alpha.omega.security.authentication.AOAuthenticationConvertor;
import com.alpha.omega.security.model.UserProfile;
import com.alpha.omega.security.token.TokenIssuerClaimsMapperService;
import com.alpha.omega.security.utils.AOSecurityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;


@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AORxAuthenticationConvertor extends AOAuthenticationConvertor implements ServerAuthenticationConverter {

	private static Logger logger = LogManager.getLogger(AORxAuthenticationConvertor.class);

	private AORxServerWebExchangeToRequestMap toRequestMap = new AORxServerWebExchangeToRequestMap();
	private AOSecurityProperties aoSecurityProperties;
	private ObjectMapper objectMapper;
	private TokenIssuerClaimsMapperService claimsMapperService;

	@Override
	public Mono<Authentication> convert(ServerWebExchange exchange) {

		final Map<String, Object> requestMap = Collections.unmodifiableMap(convertRequestHeadersToMap(exchange));

		if (!aoSecurityProperties.isMaskSensitive()){
			logger.debug("Got requestMap => {}",requestMap);
		}


		return Mono.just(requestMap)
				.map(requestMapToPreAuthenticationPrincipal(aoSecurityProperties, objectMapper, claimsMapperService))
				.map(preAuth -> {
					((UserProfile)preAuth.getPrincipal()).setAdditionalMetaData(requestMap);
					return preAuth;
				});
	}

	protected static Map<String, Object> convertRequestHeadersToMap(final ServerWebExchange exchange) {
		final HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
		Map<String, Object> attributes = CONVERSION_HEADERS.stream()
				.map(headerName -> Pair.of(headerName, exchange.getAttributes().get(headerName)))
				.filter(pair -> pair.getKey() != null && pair.getValue() != null)
				.collect(Collectors.toMap(pair -> pair.getKey(), pair -> pair.getValue()));
		Map<String, Object> headers = CONVERSION_HEADERS.stream()
				.map(headerName -> Pair.of(headerName, httpHeaders.getFirst(headerName)))
				.filter(pair -> pair.getKey() != null && pair.getValue() != null)
				.collect(Collectors.toMap(pair -> pair.getKey(), pair -> pair.getValue()));

		attributes.putAll(headers);
		// TODO Do we need all these headers or not
		//headers.remove("authorization");
		//headers.remove("Authorization");
		//headers.remove("host");
		return attributes;
	}

}
