package com.alpha.omega.security.filter;

import com.enterprise.pwc.datalabs.caching.DefaultObjectMapperFactory;
import com.enterprise.pwc.datalabs.caching.ObjectMapperFactory;
import com.enterprise.pwc.datalabs.security.response.DefaultPwcSecurityResponseExceptionFactory;
import com.enterprise.pwc.datalabs.security.response.PwcSecurityResponse;
import com.enterprise.pwc.datalabs.security.response.PwcSecurityResponseExceptionFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pwc.base.exceptions.PwcBaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.pwc.base.utils.BaseConstants.CORRELATION_ID;

public class AOServerAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

	private static Logger logger = LogManager.getLogger(AOServerAuthenticationEntryPoint.class);

	private ObjectMapper objectMapper = new DefaultObjectMapperFactory().createObjectMapper(ObjectMapperFactory.Scope.PROTOTYPE);
	private PwcSecurityResponseExceptionFactory responseExceptionFactory = new DefaultPwcSecurityResponseExceptionFactory();

	@Override
	public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
		logger.debug("commence with exception => {}", ex.getClass().getName());
		return handleResponse(exchange, ex);
	}


	public Mono<Void> handleResponse(WebFilterExchange webFilterExchange, Exception exception) {
		logger.debug("PwcServerAuthenticationEntryPoint.handleResponse with exception => {}", exception.getClass().getName());
		return this.handleResponse(webFilterExchange.getExchange(),exception);
	}
	public Mono<Void> handleResponse(ServerWebExchange exchange, Exception exception) {
		logger.debug("PwcServerAuthenticationEntryPoint.handleResponse with exception => {}", exception.getClass().getName());
		return Mono.just(exchange)
				.map(new CreateSecurityResponse(responseExceptionFactory,exception))
				.doOnNext(updateServerResponse(exchange))
				.flatMap(new CreateDataBuffer(objectMapper, exchange))
				.flatMap(dataBuffer -> {
					exchange.getResponse().getHeaders().set(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID));
					return exchange.getResponse().writeWith(Flux.just(dataBuffer));
				});
	}

	static class CreateDataBuffer implements Function<PwcSecurityResponse, Mono<DataBuffer>>{

		ObjectMapper objectMapper;
		ServerWebExchange exchange;

		public CreateDataBuffer(ObjectMapper objectMapper, ServerWebExchange exchange) {
			this.objectMapper = objectMapper;
			this.exchange = exchange;
		}

		@Override
		public Mono<DataBuffer> apply(PwcSecurityResponse pwcSecurityResponse) {
			try {
				byte[] bytes = objectMapper.writeValueAsBytes(pwcSecurityResponse);
				DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
				return Mono.just(buffer);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return Mono.error(new PwcBaseException(e));
			}

		}
	}

	static Consumer<? super PwcSecurityResponse> updateServerResponse(ServerWebExchange exchange) {
		return (securityResponse) -> {
			exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
			logger.trace("PwcSecurityResponse => {}", securityResponse);
			exchange.getResponse().setRawStatusCode(securityResponse.getStatus().value());
		};
	}

	static class CreateSecurityResponse implements Function<ServerWebExchange, PwcSecurityResponse>{

		PwcSecurityResponseExceptionFactory responseExceptionFactory;
		Exception exception;

		public CreateSecurityResponse(PwcSecurityResponseExceptionFactory responseExceptionFactory, Exception exception) {
			this.responseExceptionFactory = responseExceptionFactory;
			this.exception = exception;
		}

		@Override
		public PwcSecurityResponse apply(ServerWebExchange exchange) {
			HttpHeaders headers = new HttpHeaders();
			headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID));
			return responseExceptionFactory.createPwcSecurityResponse(exception, headers);
		}
	}

}
