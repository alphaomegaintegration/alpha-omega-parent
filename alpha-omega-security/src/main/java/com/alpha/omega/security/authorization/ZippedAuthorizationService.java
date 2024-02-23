package com.alpha.omega.security.authorization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ZippedAuthorizationService implements AuthorizationService {
	private static final Logger logger = LoggerFactory.getLogger(ZippedAuthorizationService.class);

	NameableAuthorizationService aAuthorizationService;
	NameableAuthorizationService bAuthorizationService;
	BiFunction<Tuple2<Long,Optional<AuthorizationResponse>>, Tuple2<Long,Optional<AuthorizationResponse>>, Optional<AuthorizationResponse>> zipResponse;
	Long futureGetTime = 5L;

	@PostConstruct
	public void init(){
		logger.info("Initializing ZippedAuthorizationService....");
		zipResponse = new BiZipAuthorizationResponse(aAuthorizationService, bAuthorizationService);
	}

	@Override
	public Optional<AuthorizationResponse> getAuthorizations(AuthorizationRequest authorizationRequest) {
		Optional<AuthorizationResponse> authorizationResponse = Optional.empty();
		try {
			final Map<String, String> contextMap =   MDC.getCopyOfContextMap();

			authorizationResponse = Mono.just(Tuples.of(authorizationRequest,contextMap))
					.flatMap(tuple -> Mono.zip(Mono.just(tuple.getT1())
									.publishOn(Schedulers.boundedElastic())
									.doOnNext(reqA -> {
										MDC.setContextMap(tuple.getT2());
										logger.debug("trying authorization service named {} with request {}",
											aAuthorizationService.getName(), authorizationRequest);})
									.map(getAuthorizationFrom(aAuthorizationService))
									.elapsed(),
							Mono.just(tuple.getT1())
									.publishOn(Schedulers.boundedElastic())
									.doOnNext(reqA -> {
										MDC.setContextMap(tuple.getT2());
										logger.debug("trying authorization service named {} with request {}",
												bAuthorizationService.getName(), authorizationRequest);})
									.map(getAuthorizationFrom(bAuthorizationService))
									.elapsed(),
							zipResponse))
					.toFuture()
					.get(futureGetTime, TimeUnit.SECONDS);
			if (authorizationResponse.isPresent() && !authorizationResponse.get().getErrorMessages().isEmpty()){
				logger.info("{} authorizationResponse.get().getErrorMessages() => {}",authorizationRequest.getCorrelationId(), authorizationResponse.get().getErrorMessages());
			}
		} catch (Exception e) {
			logger.error("Could not get AuthorizationResponse in ZippedAuthorizationService",e);
			if (logger.isDebugEnabled()) {
				e.printStackTrace();
			}
		}
		return authorizationResponse;
	}

	static Function<AuthorizationRequest, Optional<AuthorizationResponse>> getAuthorizationFrom(NameableAuthorizationService authorizationService){
		return (authorizationRequest) -> {
			try{
				return authorizationService.getAuthorizations(authorizationRequest);
			} catch (Exception exception){
				logger.error("{} authorizationService could not getAuthorizations ",authorizationService.getName(),exception);
				final List<String> content = Collections.singletonList(exception.getMessage());
				return Optional.of(AuthorizationResponse.newBuilder()
						.setCorrelationId(authorizationRequest.getCorrelationId())
						.setErrorMessages(content)
						.build());
			}
		};
	}

	public static final String mergedElapsedStr( Long aElapsed, Long bElapsed, String aServiceName, String bServiceName){
		return new StringBuilder().append(" Service ").append(aServiceName).append(" took ").append(aElapsed).append(" second(s). ")
				.append(" Service ").append(bServiceName).append(" took ").append(bElapsed).append(" second(s). ")
				.toString();
	}

	public static class BiZipAuthorizationResponse implements BiFunction<Tuple2<Long,Optional<AuthorizationResponse>>, Tuple2<Long,Optional<AuthorizationResponse>>, Optional<AuthorizationResponse>> {

		NameableAuthorizationService aAuthorizationService;
		NameableAuthorizationService bAuthorizationService;

		public BiZipAuthorizationResponse(NameableAuthorizationService aAuthorizationService, NameableAuthorizationService bAuthorizationService) {
			this.aAuthorizationService = aAuthorizationService;
			this.bAuthorizationService = bAuthorizationService;
		}

		@Override
		public Optional<AuthorizationResponse> apply(Tuple2<Long,Optional<AuthorizationResponse>> aTuple2, Tuple2<Long,Optional<AuthorizationResponse>> bTuple2) {

			Optional<AuthorizationResponse> aAuthorizationResponse = aTuple2.getT2();
			Optional<AuthorizationResponse> bAuthorizationResponse = bTuple2.getT2();
			logger.debug("aAuthorizationResponse.isPresent() => {} using service name => {} ", aAuthorizationResponse.isPresent(),aAuthorizationService.getName());
			logger.debug("bAuthorizationResponse.isPresent() => {} using service name => {} ", bAuthorizationResponse.isPresent(), bAuthorizationService.getName());

			aAuthorizationResponse.ifPresent(authResponse -> logger.debug("hasAuthorities => {} aAuthorizationResponse => {}",!authResponse.getAuthorities().isEmpty(),authResponse));
			bAuthorizationResponse.ifPresent(authResponse -> logger.debug("hasAuthorities => {} bAuthorizationResponse => {}",!authResponse.getAuthorities().isEmpty(),authResponse));

			boolean aHasAuthorities = aAuthorizationResponse.isPresent() ? !aAuthorizationResponse.get().getAuthorities().isEmpty() : false;
			boolean bHasAuthorities = bAuthorizationResponse.isPresent() ? !bAuthorizationResponse.get().getAuthorities().isEmpty() : false;
			Long aElapsed = aTuple2.getT1();
			Long bElapsed = bTuple2.getT1();
			String aServiceName = aAuthorizationService.getName();
			String bServiceName = bAuthorizationService.getName();

			if (aHasAuthorities & bHasAuthorities){
				return  mergeAuthorities(aAuthorizationResponse,bAuthorizationResponse, aTuple2.getT1(), bTuple2.getT1(),
						aAuthorizationService.getName(), bAuthorizationService.getName());
			}
			else if (aHasAuthorities & !bHasAuthorities) {
				return Optional.of(AuthorizationResponse.fromElapsed(aAuthorizationResponse.get(), () -> aElapsed,
						() -> mergedElapsedStr(aElapsed, bElapsed, aServiceName, bServiceName)));
			} else if (!aHasAuthorities & bHasAuthorities) {
				return Optional.of(AuthorizationResponse.fromElapsed(bAuthorizationResponse.get(), () -> aElapsed,
						() -> mergedElapsedStr(aElapsed, bElapsed, aServiceName, bServiceName)));
			} else {
				String correlationId = aAuthorizationResponse.get().getCorrelationId();
				Collection<String> errorString = mergeErrors(aAuthorizationResponse.get(), bAuthorizationResponse.get());
				return Optional.of(AuthorizationResponse.newBuilder()
						.setCorrelationId(correlationId)
						.setErrorMessages(errorString)
						.build());
			}
		}

		private Optional<AuthorizationResponse> mergeAuthorities(Optional<AuthorizationResponse> aAuthorizationResponse,
																 Optional<AuthorizationResponse> bAuthorizationResponse,
																 Long aElapsed, Long bElapsed, String aServiceName, String bServiceName) {
			aAuthorizationResponse.get().getErrorMessages().addAll(bAuthorizationResponse.get().getErrorMessages());
			if (!aAuthorizationResponse.get().getAuthorities().isEmpty()){
				aAuthorizationResponse.get().getAuthorities().addAll(bAuthorizationResponse.get().getAuthorities());
			}
			return Optional.of(AuthorizationResponse.fromElapsed(aAuthorizationResponse.get(), () -> aElapsed,
					() -> mergedElapsedStr(aElapsed, bElapsed, aServiceName, bServiceName)));
		}

		private Collection<String> mergeErrors(AuthorizationResponse aAuthorizationResponse, AuthorizationResponse bAuthorizationResponse) {
			ArrayList<String> errors = new ArrayList<>();
			errors.addAll(aAuthorizationResponse.getErrorMessages());
			errors.addAll(bAuthorizationResponse.getErrorMessages());
			return errors;
		}


	}

	public static Builder newBuilder() {
		return new Builder();
	}


	public static final class Builder {
		NameableAuthorizationService aAuthorizationService;
		NameableAuthorizationService bAuthorizationService;
		BiFunction<Tuple2<Long, Optional<AuthorizationResponse>>, Tuple2<Long,Optional<AuthorizationResponse>>, Optional<AuthorizationResponse>> zipResponse;
		Long futureGetTime = 5L;

		private Builder() {
		}

		public static Builder aZippedAuthorizationService() {
			return new Builder();
		}

		public Builder setAAuthorizationService(NameableAuthorizationService aAuthorizationService) {
			this.aAuthorizationService = aAuthorizationService;
			return this;
		}

		public Builder setBAuthorizationService(NameableAuthorizationService bAuthorizationService) {
			this.bAuthorizationService = bAuthorizationService;
			return this;
		}

		public Builder setZipResponse(BiFunction<Tuple2<Long, Optional<AuthorizationResponse>>, Tuple2<Long, Optional<AuthorizationResponse>>, Optional<AuthorizationResponse>> zipResponse) {
			this.zipResponse = zipResponse;
			return this;
		}

		public Builder setFutureGetTime(Long futureGetTime) {
			this.futureGetTime = futureGetTime;
			return this;
		}

		public ZippedAuthorizationService build() {
			ZippedAuthorizationService zippedAuthorizationService = new ZippedAuthorizationService();
			zippedAuthorizationService.zipResponse = this.zipResponse;
			zippedAuthorizationService.bAuthorizationService = this.bAuthorizationService;
			zippedAuthorizationService.futureGetTime = this.futureGetTime;
			zippedAuthorizationService.aAuthorizationService = this.aAuthorizationService;
			return zippedAuthorizationService;
		}
	}
}
