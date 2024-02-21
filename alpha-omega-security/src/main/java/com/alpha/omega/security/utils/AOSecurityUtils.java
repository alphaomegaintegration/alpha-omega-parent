package com.alpha.omega.security.utils;

import com.alpha.omega.cache.DefaultObjectMapperFactory;
import com.alpha.omega.cache.ObjectMapperFactory;
import com.alpha.omega.core.exception.AOBaseException;
import com.alpha.omega.security.authentication.UserProfileAuthentication;
import com.alpha.omega.security.filter.AOServerAuthenticationEntryPoint;
import com.alpha.omega.security.model.UserProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alpha.omega.core.Constants.*;
import static com.alpha.omega.security.utils.AOSecurityConstants.SECURITY_AUTHENTICATION;


public class AOSecurityUtils {
	public static final String ACCESS_DENIED = "ACCESS DENIED";
	public static final String ACCESS_DENIED_FORMAT = ACCESS_DENIED + " %s";
	public static final String AO_SECURITY_UTILS_CHECK_DECISION_WAS_FALSE = "AOSecurityUtils.checkDecision was false";
	public static final String NO_SECURITY_CONTEXT = "No security context";
	public static final String AO_SECURITY_UTILS_DECIDE_PERMISSION_ACCESS_EXCEPTION = "AOSecurityUtils.decidePermissionAccess exception";
	private static Logger logger = LogManager.getLogger(AOSecurityUtils.class);
	private static final ObjectMapper objectMapper = new DefaultObjectMapperFactory().createObjectMapper(ObjectMapperFactory.Scope.SINGLETON);

	private static AOServerAuthenticationEntryPoint errorMapper = new AOServerAuthenticationEntryPoint();

	public static Object convertToObj(String object, Class<?> type) {
		try {
			return objectMapper.readValue(new String(java.util.Base64.getDecoder().decode(object), StandardCharsets.UTF_8), type);
		} catch (IOException e) {
			logger.error("unable to convert JWT header:", e);
			StringBuilder message = new StringBuilder("\"unable to convert type => ");
			message.append(type.getName());
			throw new AOBaseException(message.toString(), e);
		}
	}

	public static <T> Function<String, Mono<T>> cacheDuration(Duration duration,Function<String, Mono<T>> fn) {
		final Cache<String, T> cache = Caffeine.newBuilder()
				.expireAfterWrite(duration.toMillis(), TimeUnit.MILLISECONDS)
				.recordStats()
				.build();
		return key -> {
			T result = cache.getIfPresent(key);
			if (result != null) {
				return Mono.just(result);
			} else {
				return fn.apply(key).doOnNext(n -> cache.put(key, n));
			}

		};

	}

	public static Mono<UserProfile> userProfileFromMonoSecurityContext(Mono<SecurityContext> securityContextMono, UserProfile altUserprofile){
		return securityContextMono.map(securityContext -> userProfileFromAuthentication(securityContext.getAuthentication()))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.switchIfEmpty(Mono.justOrEmpty(altUserprofile));
	}

	public static final Function<Boolean, Authentication> checkDecision(final Authentication authentication){
		return (decision) -> {
			if (!decision){
				throw new AccessDeniedException(String.format(ACCESS_DENIED_FORMAT, AO_SECURITY_UTILS_CHECK_DECISION_WAS_FALSE));
			} else {
				return authentication;
			}

		};
	}

	public static final Mono<Authentication> decidePermissionAccess(BiFunction<Authentication, String, Mono<Boolean>> decisionFunction,
																	String permission,
																	ServerWebExchange webExchange){
		final Authentication authentication = (Authentication)webExchange.getAttributes().get(SECURITY_AUTHENTICATION);
		return Mono.just(authentication)
				.switchIfEmpty(Mono.defer(() -> Mono.error(new AccessDeniedException(String.format(ACCESS_DENIED_FORMAT, NO_SECURITY_CONTEXT)))))
				.flatMap(auth -> decisionFunction.apply(auth, permission))
				.doOnError(e -> logger.error("Error using decisionFunction => ",e))
				.onErrorResume(e -> Mono.error(new AccessDeniedException(String.format(ACCESS_DENIED_FORMAT, AO_SECURITY_UTILS_DECIDE_PERMISSION_ACCESS_EXCEPTION))))
				.map(checkDecision(authentication));
	}

	public static Optional<UserProfile> userProfileFromAuthentication(Authentication authentication){
		UserProfile userProfile = null;
		try{
			UserProfileAuthentication userProfileAuthentication = (UserProfileAuthentication) authentication;
			userProfile = userProfileAuthentication.getUserProfile();
		} catch (Exception e){
			logger.warn("Could not extract UserProfile from Authentication ",e);
		}
		return Optional.ofNullable(userProfile);
	}

	public static Optional<UserProfile> userProfileFromSecurityContext(){
		return userProfileFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
	}

	public static String findAdminUser(ServerWebExchange exchange, UserProfile defaultServiceAccount) {
		UserProfileAuthentication authentication = (UserProfileAuthentication) exchange.getAttributes().get(AOSecurityConstants.SECURITY_AUTHENTICATION);
		Optional<UserProfile> userProfileOptional = AOSecurityUtils.userProfileFromAuthentication(authentication);
		String admin = userProfileOptional.isPresent() ? userProfileOptional.get().getName(): defaultServiceAccount.getName();
		return admin;
	}

	public static <T> T convertObjectToAnother(Object obj, Class<T> clazz) {
		if (Objects.isNull(obj)) return null;
		try {
			return objectMapper.convertValue(obj, clazz);
		} catch (Exception e) {
			logger.error("", e);
			throw new IllegalArgumentException("", e);
		}
	}

	public static List<String> getStackTraceAsList(Throwable throwable) {
		List<String> stringList =  new ArrayList<>();
		stringList.add(new StringBuilder(throwable.getClass().getName()).append(COLON)
				.append(SPACE_STR).append(throwable.getMessage()).toString());
		if (throwable != null){
			List<String> errorList = Arrays.asList(throwable.getStackTrace()).stream()
					.map(StackTraceElement::toString)
					.collect(Collectors.toList());
			stringList.addAll(errorList);
		}

		return stringList;
	}

	public static Tuple2<String, String> parseBasicAuthString(final String authHeader) {
		final String userStr = authHeader.replace(BASIC, "");
		byte[] decoded = java.util.Base64.getDecoder().decode(userStr.getBytes(StandardCharsets.UTF_8));
		String decoded_userName_password = new String(decoded, StandardCharsets.UTF_8);
		String username = decoded_userName_password.split(":")[0];
		String password = decoded_userName_password.substring(username.length() + 1);
		return Tuples.of(username, password);
	}


	public static <T> T convertStringToObjectNoException(String string, Class<T> clazz) {
		if (StringUtils.isEmpty(string)) return null;
		try {
			return objectMapper.readValue(string, clazz);
		} catch (IOException e) {
			logger.error("Unable to Convert String to Object", e);
			return null;
		}
	}


	public static <T> Optional<T> convertStringToObjectNoExceptionOptional(String string, Class<T> clazz) {
		T t = null;
		if (!StringUtils.isEmpty(string)){
			try {
				t =  objectMapper.readValue(string, clazz);
			} catch (IOException e) {
				logger.error("convertStringToObjectNoExceptionOptional Unable to Convert String to Object", e);
			}
		}
		return Optional.ofNullable(t);
	}

}
