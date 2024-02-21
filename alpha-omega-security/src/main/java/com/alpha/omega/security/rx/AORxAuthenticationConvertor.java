package com.alpha.omega.security.rx;

import com.enterprise.pwc.datalabs.security.PwcSecurityProperties;
import com.enterprise.pwc.datalabs.security.authentication.PwcAuthenticationConvertor;
import com.enterprise.pwc.datalabs.security.token.issuer.TokenIssuerClaimsMapperService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pwc.base.model.UserProfile;
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


public class AORxAuthenticationConvertor extends PwcAuthenticationConvertor implements ServerAuthenticationConverter {

	private static Logger logger = LogManager.getLogger(AORxAuthenticationConvertor.class);

	private PwcRxServerWebExchangeToRequestMap toRequestMap = new PwcRxServerWebExchangeToRequestMap();
	private PwcSecurityProperties pwcSecurityProperties;
	private ObjectMapper objectMapper;
	private TokenIssuerClaimsMapperService claimsMapperService;

	public AORxAuthenticationConvertor(PwcSecurityProperties pwcSecurityProperties,
									   TokenIssuerClaimsMapperService claimsMapperService) {
		super(pwcSecurityProperties,claimsMapperService);
		this.pwcSecurityProperties = pwcSecurityProperties;
		this.claimsMapperService = claimsMapperService;
	}

	@Override
	public Mono<Authentication> convert(ServerWebExchange exchange) {

		final Map<String, Object> requestMap = Collections.unmodifiableMap(convertRequestHeadersToMap(exchange));

		if (!pwcSecurityProperties.isMaskSensitive()){
			logger.debug("Got requestMap => {}",requestMap);
		}


		return Mono.just(requestMap)
				.map(requestMapToPreAuthenticationPrincipal(pwcSecurityProperties, objectMapper, claimsMapperService))
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

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private PwcRxServerWebExchangeToRequestMap toRequestMap = new PwcRxServerWebExchangeToRequestMap();
		private PwcSecurityProperties pwcSecurityProperties;
		private ObjectMapper objectMapper;
		private TokenIssuerClaimsMapperService claimsMapperService;

		private Builder() {
		}

		public static Builder aPwcRxAuthenticationConvertor() {
			return new Builder();
		}

		public Builder setToRequestMap(PwcRxServerWebExchangeToRequestMap toRequestMap) {
			this.toRequestMap = toRequestMap;
			return this;
		}

		public Builder setPwcSecurityProperties(PwcSecurityProperties pwcSecurityProperties) {
			this.pwcSecurityProperties = pwcSecurityProperties;
			return this;
		}

		public Builder setObjectMapper(ObjectMapper objectMapper) {
			this.objectMapper = objectMapper;
			return this;
		}

		public Builder setClaimsMapperService(TokenIssuerClaimsMapperService claimsMapperService) {
			this.claimsMapperService = claimsMapperService;
			return this;
		}

		public AORxAuthenticationConvertor build() {
			AORxAuthenticationConvertor pwcRxAuthenticationConvertor = new AORxAuthenticationConvertor(pwcSecurityProperties,claimsMapperService);
			pwcRxAuthenticationConvertor.setObjectMapper(objectMapper);
			pwcRxAuthenticationConvertor.toRequestMap = this.toRequestMap;
			return pwcRxAuthenticationConvertor;
		}
	}
}
