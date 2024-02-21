package com.alpha.omega.security.filter;

import com.pwc.base.filter.tracing.LogFilterHandler;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.reactive.resource.ResourceUrlProvider;
import org.springframework.web.server.ServerWebExchange;

import java.util.*;

public class AORxSecurityWebFilterChainRequest {

	private Set<String> protectedUrls = new HashSet<>();
	private Map<String, HttpMethod> protectedUrlsMethod = new LinkedHashMap<>();
	private List<String> excludeUrls = new ArrayList<>();
	private ServerHttpSecurity httpSecurity;
	private List<WebFilterPosition> webFilters = new ArrayList<>();
	private ReactiveAuthenticationManagerResolver<ServerWebExchange> resolver;
	private ServerAuthenticationConverter converter;
	private String loggableRequestHeadersStr;
	private LogFilterHandler logFilterHandler;
	private boolean disableCSRF = Boolean.TRUE;
	private ResourceUrlProvider resourceUrlProvider;

	public Set<String> getProtectedUrls() {
		return protectedUrls;
	}

	public List<String> getExcludeUrls() {
		return excludeUrls;
	}

	public ServerHttpSecurity getHttpSecurity() {
		return httpSecurity;
	}

	public List<WebFilterPosition> getWebFilters() {
		return webFilters;
	}

	public ReactiveAuthenticationManagerResolver<ServerWebExchange> getResolver() {
		return resolver;
	}

	public ServerAuthenticationConverter getConverter() {
		return converter;
	}

	public String getLoggableRequestHeadersStr() {
		return loggableRequestHeadersStr;
	}

	public LogFilterHandler getLogFilterHandler() {
		return logFilterHandler;
	}

	public boolean isDisableCSRF() {
		return disableCSRF;
	}

	public Map<String, HttpMethod> getProtectedUrlsMethod() {
		return protectedUrlsMethod;
	}

	public static Builder newBuilder(){
		return new Builder();
	}

	public ResourceUrlProvider getResourceUrlProvider() {
		return resourceUrlProvider;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("PwcRxSecurityWebFilterChainRequest{");
		sb.append("protectedUrls=").append(protectedUrls);
		sb.append(", protectedUrlsMethod=").append(protectedUrlsMethod);
		sb.append(", excludeUrls=").append(excludeUrls);
		sb.append(", httpSecurity=").append(httpSecurity);
		sb.append(", webFilters=").append(webFilters);
		sb.append(", resolver=").append(resolver);
		sb.append(", converter=").append(converter);
		sb.append(", loggableRequestHeadersStr='").append(loggableRequestHeadersStr).append('\'');
		sb.append(", logFilterHandler=").append(logFilterHandler);
		sb.append(", disableCSRF=").append(disableCSRF);
		sb.append(", resourceUrlProvider=").append(resourceUrlProvider);
		sb.append('}');
		return sb.toString();
	}

	public static final class Builder {
		private Set<String> protectedUrls = new HashSet<>();
		private Map<String, HttpMethod> protectedUrlsMethod = new LinkedHashMap<>();
		private List<String> excludeUrls = new ArrayList<>();
		private ServerHttpSecurity httpSecurity;
		private List<WebFilterPosition> webFilters = new ArrayList<>();
		private ReactiveAuthenticationManagerResolver<ServerWebExchange> resolver;
		private ServerAuthenticationConverter converter;
		private String loggableRequestHeadersStr;
		private LogFilterHandler logFilterHandler;
		private boolean disableCSRF = Boolean.TRUE;
		private ResourceUrlProvider resourceUrlProvider;

		private Builder() {
		}

		public static Builder aPwcRxSecurityWebFilterChainRequest() {
			return new Builder();
		}

		public Builder setProtectedUrls(Set<String> protectedUrls) {
			this.protectedUrls = protectedUrls;
			return this;
		}

		public Builder setProtectedUrlsMethod(Map<String, HttpMethod> protectedUrlsMethod) {
			this.protectedUrlsMethod = protectedUrlsMethod;
			return this;
		}

		public Builder setExcludeUrls(List<String> excludeUrls) {
			this.excludeUrls = excludeUrls;
			return this;
		}

		public Builder setHttpSecurity(ServerHttpSecurity httpSecurity) {
			this.httpSecurity = httpSecurity;
			return this;
		}

		public Builder setWebFilters(List<WebFilterPosition> webFilters) {
			this.webFilters = webFilters;
			return this;
		}

		public Builder setResolver(ReactiveAuthenticationManagerResolver<ServerWebExchange> resolver) {
			this.resolver = resolver;
			return this;
		}

		public Builder setConverter(ServerAuthenticationConverter converter) {
			this.converter = converter;
			return this;
		}

		public Builder setLoggableRequestHeadersStr(String loggableRequestHeadersStr) {
			this.loggableRequestHeadersStr = loggableRequestHeadersStr;
			return this;
		}

		public Builder setLogFilterHandler(LogFilterHandler logFilterHandler) {
			this.logFilterHandler = logFilterHandler;
			return this;
		}

		public Builder setDisableCSRF(boolean disableCSRF) {
			this.disableCSRF = disableCSRF;
			return this;
		}

		public Builder setResourceUrlProvider(ResourceUrlProvider resourceUrlProvider) {
			this.resourceUrlProvider = resourceUrlProvider;
			return this;
		}

		public AORxSecurityWebFilterChainRequest build() {
			AORxSecurityWebFilterChainRequest AORxSecurityWebFilterChainRequest = new AORxSecurityWebFilterChainRequest();
			AORxSecurityWebFilterChainRequest.protectedUrls = this.protectedUrls;
			AORxSecurityWebFilterChainRequest.protectedUrlsMethod = this.protectedUrlsMethod;
			AORxSecurityWebFilterChainRequest.converter = this.converter;
			AORxSecurityWebFilterChainRequest.httpSecurity = this.httpSecurity;
			AORxSecurityWebFilterChainRequest.webFilters = this.webFilters;
			AORxSecurityWebFilterChainRequest.resolver = this.resolver;
			AORxSecurityWebFilterChainRequest.excludeUrls = this.excludeUrls;
			AORxSecurityWebFilterChainRequest.logFilterHandler = this.logFilterHandler;
			AORxSecurityWebFilterChainRequest.loggableRequestHeadersStr = this.loggableRequestHeadersStr;
			AORxSecurityWebFilterChainRequest.disableCSRF = this.disableCSRF;
			AORxSecurityWebFilterChainRequest.resourceUrlProvider = this.resourceUrlProvider;
			AORxSecurityWebFilterChainRequest.webFilters = this.webFilters;
			return AORxSecurityWebFilterChainRequest;
		}
	}
}
