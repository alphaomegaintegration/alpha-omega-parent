package com.alpha.omega.security.filter;

import jakarta.servlet.Filter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.*;

public class AOSecurityFilterChainRequest {

	private List<Filter> filters = new ArrayList<>();
	private Map<String, HttpMethod> protectedUrlsMethod = new LinkedHashMap<>();
	private Set<String> protectedUrls = new LinkedHashSet<>();
	private List<String> excludeUrls = new ArrayList<>();
	private HttpSecurity httpSecurity;
	private boolean disableCSRF = Boolean.TRUE;

	public Map<String, HttpMethod> getProtectedUrlsMethod() {
		return protectedUrlsMethod;
	}

	public void setProtectedUrlsMethod(Map<String, HttpMethod> protectedUrlsMethod) {
		this.protectedUrlsMethod = protectedUrlsMethod;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public Set<String> getProtectedUrls() {
		return protectedUrls;
	}

	public List<String> getExcludeUrls() {
		return excludeUrls;
	}

	public HttpSecurity getHttpSecurity() {
		return httpSecurity;
	}

	public boolean isDisableCSRF() {
		return disableCSRF;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public void setProtectedUrls(Set<String> protectedUrls) {
		this.protectedUrls = protectedUrls;
	}

	public void setExcludeUrls(List<String> excludeUrls) {
		this.excludeUrls = excludeUrls;
	}

	public void setHttpSecurity(HttpSecurity httpSecurity) {
		this.httpSecurity = httpSecurity;
	}

	public void setDisableCSRF(boolean disableCSRF) {
		this.disableCSRF = disableCSRF;
	}

	public static Builder newBuilder(){
		return new Builder();
	}

	public AOSecurityFilterChainRequest(List<Filter> filters, Set<String> protectedUrls, List<String> excludeUrls,
										HttpSecurity httpSecurity, boolean disableCSRF, Map<String, HttpMethod> protectedUrlsMethod) {
		this.filters = filters;
		this.protectedUrls = protectedUrls;
		this.excludeUrls = excludeUrls;
		this.httpSecurity = httpSecurity;
		this.disableCSRF = disableCSRF;
		this.protectedUrlsMethod = protectedUrlsMethod;
	}

	public AOSecurityFilterChainRequest() {
	}

	public static final class Builder {
		private List<Filter> filters = new ArrayList<>();
		private Map<String, HttpMethod> protectedUrlsMethod = new LinkedHashMap<>();
		private Set<String> protectedUrls = new LinkedHashSet<>();
		private List<String> excludeUrls = new ArrayList<>();
		private HttpSecurity httpSecurity;
		private boolean disableCSRF = Boolean.TRUE;

		private Builder() {
		}

		public static Builder aPwcSecurityFilterChainRequest() {
			return new Builder();
		}

		public Builder setFilters(List<Filter> filters) {
			this.filters = filters;
			return this;
		}

		public Builder setProtectedUrlsMethod(Map<String, HttpMethod> protectedUrlsMethod) {
			this.protectedUrlsMethod = protectedUrlsMethod;
			return this;
		}

		public Builder setProtectedUrls(Set<String> protectedUrls) {
			this.protectedUrls = protectedUrls;
			return this;
		}

		public Builder setExcludeUrls(List<String> excludeUrls) {
			this.excludeUrls = excludeUrls;
			return this;
		}

		public Builder setHttpSecurity(HttpSecurity httpSecurity) {
			this.httpSecurity = httpSecurity;
			return this;
		}

		public Builder setDisableCSRF(boolean disableCSRF) {
			this.disableCSRF = disableCSRF;
			return this;
		}

		public AOSecurityFilterChainRequest build() {
			return new AOSecurityFilterChainRequest(filters, protectedUrls, excludeUrls, httpSecurity, disableCSRF, protectedUrlsMethod);
		}
	}
}
