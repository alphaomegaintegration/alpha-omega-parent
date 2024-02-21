package com.alpha.omega.security.filter;

import com.alpha.omega.cache.DefaultObjectMapperFactory;
import com.alpha.omega.cache.ObjectMapperFactory;
import com.alpha.omega.core.exception.AOBaseException;
import com.alpha.omega.security.response.AOSecurityResponse;
import com.alpha.omega.security.response.AOSecurityResponseExceptionFactory;
import com.alpha.omega.security.response.DefaultAOSecurityResponseExceptionFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static com.alpha.omega.core.Constants.CORRELATION_ID;


public class AOServerAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

	private static Logger logger = LogManager.getLogger(AOServerAuthenticationEntryPoint.class);

	private ObjectMapper objectMapper = new DefaultObjectMapperFactory().createObjectMapper(ObjectMapperFactory.Scope.PROTOTYPE);
	private AOSecurityResponseExceptionFactory responseExceptionFactory = new DefaultAOSecurityResponseExceptionFactory();

	@Override
	public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
		logger.debug("commence with exception => {}", ex.getClass().getName());
		return handleResponse(exchange, ex);
	}


	public Mono<Void> handleResponse(WebFilterExchange webFilterExchange, Exception exception) {
		logger.debug("aoServerAuthenticationEntryPoint.handleResponse with exception => {}", exception.getClass().getName());
		return this.handleResponse(webFilterExchange.getExchange(),exception);
	}
	public Mono<Void> handleResponse(ServerWebExchange exchange, Exception exception) {
		logger.debug("aoServerAuthenticationEntryPoint.handleResponse with exception => {}", exception.getClass().getName());
		return Mono.just(exchange)
				.map(new CreateSecurityResponse(responseExceptionFactory,exception))
				.doOnNext(updateServerResponse(exchange))
				.flatMap(new CreateDataBuffer(objectMapper, exchange))
				.flatMap(dataBuffer -> {
					exchange.getResponse().getHeaders().set(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID));
					return exchange.getResponse().writeWith(Flux.just(dataBuffer));
				});
	}

	static class CreateDataBuffer implements Function<AOSecurityResponse, Mono<DataBuffer>>{

		ObjectMapper objectMapper;
		ServerWebExchange exchange;

		public CreateDataBuffer(ObjectMapper objectMapper, ServerWebExchange exchange) {
			this.objectMapper = objectMapper;
			this.exchange = exchange;
		}

		@Override
		public Mono<DataBuffer> apply(AOSecurityResponse aoSecurityResponse) {
			try {
				byte[] bytes = objectMapper.writeValueAsBytes(aoSecurityResponse);
				DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
				return Mono.just(buffer);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return Mono.error(new AOBaseException(e));
			}

		}
	}

	static Consumer<? super AOSecurityResponse> updateServerResponse(ServerWebExchange exchange) {
		return (securityResponse) -> {
			exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
			logger.trace("aoSecurityResponse => {}", securityResponse);
			exchange.getResponse().setRawStatusCode(securityResponse.getStatus().value());
		};
	}

	static class CreateSecurityResponse implements Function<ServerWebExchange, AOSecurityResponse>{

		AOSecurityResponseExceptionFactory responseExceptionFactory;
		Exception exception;

		public CreateSecurityResponse(AOSecurityResponseExceptionFactory responseExceptionFactory, Exception exception) {
			this.responseExceptionFactory = responseExceptionFactory;
			this.exception = exception;
		}

		@Override
		public AOSecurityResponse apply(ServerWebExchange exchange) {
			HttpHeaders headers = new HttpHeaders();
			headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID));
			return responseExceptionFactory.createAOSecurityResponse(exception, headers);
		}
	}

}
