package com.alpha.omega.security.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.web.reactive.context.ReactiveWebServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.server.util.matcher.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This mathcer gathers all the request mapping urls and additionalUrls to have the overall urls for the service.
 * It then merges these urls, and negates the match essentially forming a 404 not found mathcer
 */

public class UrlNotFoundServerWebExchangeMatcher implements ServerWebExchangeMatcher,
		ApplicationListener<ApplicationEvent>, BeanPostProcessor{
	private static Logger logger = LogManager.getLogger(UrlNotFoundServerWebExchangeMatcher.class);
	private static final PathPatternParser DEFAULT_PATTERN_PARSER = new PathPatternParser();
	private ServerWebExchangeMatcher serverWebExchangeMatcher = ServerWebExchangeMatchers.anyExchange();
	Set<String> additionalUrls = new HashSet<>();
	int maxConfiguredUrls = 0;
	boolean configuredUrls = false;
	Map<String, Integer> eventCount = new HashMap<>();

	public UrlNotFoundServerWebExchangeMatcher() {
		logger.info("Creating UrlNotFoundServerWebExchangeMatcher.....");
	}

	public UrlNotFoundServerWebExchangeMatcher(Set<String> additionalUrls) {
		this.additionalUrls = additionalUrls;
		logger.info("Creating UrlNotFoundServerWebExchangeMatcher.....with additionalUrls => {} ",this.additionalUrls);
	}

	@Override
	public Mono<MatchResult> matches(ServerWebExchange exchange) {
		return serverWebExchangeMatcher.matches(exchange);
	}

	// TODO ugly refactor
	/*
	@Override
	public void onApplicationEvent(ApplicationEvent event) {

		boolean interestedEvent = (event instanceof ContextRefreshedEvent | event instanceof ContextStartedEvent |
				event instanceof ApplicationStartedEvent | event instanceof AvailabilityChangeEvent |
				event instanceof ReactiveWebServerInitializedEvent);

		eventCount.compute(event.getClass().getName(), (evName, cnt) -> {
			return cnt == null ? new Integer(1):cnt++;
		});

		logger.debug("UrlNotFoundServerWebExchangeMatcher.onApplicationEvent {} interested event => {} with eventCount => {}",
				new Object[]{event.getClass().getName(),interestedEvent, eventCount});
		ApplicationContext applicationContext = null;
		if (interestedEvent){
			if (event instanceof ContextRefreshedEvent ){
				applicationContext = ((ApplicationContextEvent) event).getApplicationContext();
			} else if (event instanceof ContextStartedEvent){

				applicationContext = ((ApplicationContextEvent) event).getApplicationContext();
			} else if (event instanceof ApplicationStartedEvent){
				applicationContext = ((ApplicationStartedEvent)event).getApplicationContext();
			} else if (event instanceof AvailabilityChangeEvent){


				try{
					AvailabilityChangeEvent  ace = (AvailabilityChangeEvent)event;
					applicationContext = ((ApplicationContext)ace.getSource());
				} catch (ClassCastException cce){
					logger.warn("Could not cast ",cce);
				}

			}

			if (applicationContext != null  && !configuredUrls){
				RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
						.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
				postProcess(requestMappingHandlerMapping);
			} else {

			}

		}
	}

	 */

	@Override
	public void onApplicationEvent(ApplicationEvent event) {

		boolean interestedEvent = (event instanceof ContextRefreshedEvent | event instanceof ContextStartedEvent |
				event instanceof ApplicationStartedEvent | event instanceof AvailabilityChangeEvent |
				event instanceof ReactiveWebServerInitializedEvent);

		eventCount.compute(event.getClass().getName(), (evName, cnt) -> {
			return cnt == null ? new Integer(1) : cnt++;
		});

		logger.debug("UrlNotFoundServerWebExchangeMatcher.onApplicationEvent {} interested event => {} with eventCount => {}",
				new Object[]{event.getClass().getName(), interestedEvent, eventCount});
	}


	@EventListener(classes = {ReactiveWebServerInitializedEvent.class})
	public void onReactiveApplicationEvent(ReactiveWebServerInitializedEvent event) {

		ApplicationContext applicationContext = event.getApplicationContext();
		RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
				.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
		logger.info("onReactiveApplicationEvent Calling postProcess with event => {}",event);
		postProcess(requestMappingHandlerMapping);

	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean instanceof RequestMappingHandlerMapping ? this.postProcess((RequestMappingHandlerMapping)bean) : bean;
	}

	private Object postProcess(RequestMappingHandlerMapping bean) {
		Map<RequestMappingInfo, HandlerMethod> map = bean.getHandlerMethods();
		logger.info("UrlNotFoundServerWebExchangeMatcher.postProcess Got RequestMappingInfo, HandlerMethod map size() => {} and confiugurdUrls", map.size());

		if (!map.isEmpty()){
			List<ServerWebExchangeMatcher> matchersList =  map.entrySet().stream()
					.map(entry -> entry.getKey().getPatternsCondition().getPatterns())
					.flatMap(patterns -> patterns.stream())
					.map(pattern -> new PathPatternParserServerWebExchangeMatcher(pattern))
					.collect(Collectors.toList());
			List<ServerWebExchangeMatcher> additionalMatchers = additionalUrls.stream()
					.map(pattern -> new PathPatternParserServerWebExchangeMatcher(pattern))
					.collect(Collectors.toList());
			matchersList.addAll(additionalMatchers);
			if (matchersList.size() > 0 && !configuredUrls){
				map.forEach((key, value) -> {
					logger.info("UrlNotFoundServerWebExchangeMatcher.postProcess RequestMappingInfo =>{} HandlerMethod =>{}", key, value);
					logger.info("UrlNotFoundServerWebExchangeMatcher.postProcess Adding patterns to NotFoundMatcher => {}", key.getPatternsCondition().getPatterns());
				});
				logger.info("UrlNotFoundServerWebExchangeMatcher.postProcess matchersList => {}",matchersList);
				OrServerWebExchangeMatcher orServerWebExchangeMatcher = new OrServerWebExchangeMatcher(matchersList);
				NegatedServerWebExchangeMatcher negatedServerWebExchangeMatcher = new NegatedServerWebExchangeMatcher(orServerWebExchangeMatcher);
				serverWebExchangeMatcher = negatedServerWebExchangeMatcher;
				configuredUrls = Boolean.TRUE.booleanValue();
			} else {
				logger.warn("Not configuring with less urls...");
			}


		} else {
			logger.info("UrlNotFoundServerWebExchangeMatcher.postProcess No mapping => {} ", map);
		}
		return bean;

	}


	/*
	static class WebFilterChainPostProcessor implements BeanPostProcessor {
		WebFilterChainPostProcessor() {
		}

		public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
			return bean instanceof WebFilterChainProxy ? this.postProcess((WebFilterChainProxy)bean) : bean;
		}

		private WebFilterChainProxy postProcess(WebFilterChainProxy existing) {
			ServerWebExchangeMatcher cloudFoundryRequestMatcher = ServerWebExchangeMatchers.pathMatchers(new String[]{"/cloudfoundryapplication/**"});
			WebFilter noOpFilter = (exchange, chain) -> {
				return chain.filter(exchange);
			};
			MatcherSecurityWebFilterChain ignoredRequestFilterChain = new MatcherSecurityWebFilterChain(cloudFoundryRequestMatcher, Collections.singletonList(noOpFilter));
			MatcherSecurityWebFilterChain allRequestsFilterChain = new MatcherSecurityWebFilterChain(ServerWebExchangeMatchers.anyExchange(), Collections.singletonList(existing));
			return new WebFilterChainProxy(new SecurityWebFilterChain[]{ignoredRequestFilterChain, allRequestsFilterChain});
		}
	}

	 */
}

