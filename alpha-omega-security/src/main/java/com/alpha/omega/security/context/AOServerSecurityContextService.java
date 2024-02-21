package com.alpha.omega.security.context;

import com.alpha.omega.cache.CacheDao;
import com.alpha.omega.cache.expiration.Expiration;
import com.alpha.omega.security.authentication.UserProfileAuthentication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.alpha.omega.security.utils.AOSecurityUtils.convertStringToObjectNoException;

public class AOServerSecurityContextService implements ServerSecurityContextRepository {

	private static Logger logger = LogManager.getLogger(AOServerSecurityContextService.class);
	final static String CONTEXT_NAMESPACE = "security.context.namespace";

	CacheDao cacheDao;
	ObjectMapper objectMapper;
	Expiration securityExpiration;

	public AOServerSecurityContextService(CacheDao cacheDao, ObjectMapper objectMapper, Expiration securityExpiration) {
		this.cacheDao = cacheDao;
		this.objectMapper = objectMapper;
		this.securityExpiration = securityExpiration;
	}

	@Override
	public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
		return Mono.just(context)
				.doOnNext(ctx -> {
					String cacheKey = exchange.getRequest().getId();
					String value = "";
					try {
						value = objectMapper.writeValueAsString(context.getAuthentication());
						cacheDao.putInCache(CONTEXT_NAMESPACE, cacheKey, value, securityExpiration);
					} catch (JsonProcessingException e) {
						if (logger.isDebugEnabled()) {
							e.printStackTrace();
						}
						logger.warn("Could not parse " + context.getAuthentication());
					}

				}).then();
	}

	@Override
	public Mono<SecurityContext> load(ServerWebExchange exchange) {
		return Mono.just(exchange)
				.map(exch -> exch.getRequest().getId())
				.map(cKey -> cacheDao.getObjectFromCacheOptional(CONTEXT_NAMESPACE, cKey, String.class))
				.filter(Optional::isPresent)
				.doOnNext((val) -> logger.info("Got value from cache {}",val))
				.map(cacheVal ->  convertStringToObjectNoException(cacheVal.get(), UserProfileAuthentication.class))
				.map(auth -> new SecurityContextImpl(auth));
	}

}
