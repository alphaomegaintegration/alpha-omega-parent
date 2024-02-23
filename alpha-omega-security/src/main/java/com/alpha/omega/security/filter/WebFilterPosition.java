package com.alpha.omega.security.filter;

import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.web.server.WebFilter;

public class WebFilterPosition {
	public enum OtherWebFilter{
		BEFORE, AT, AFTER;
	}


	private WebFilter webFilter;
	private OtherWebFilter position = OtherWebFilter.AT;
	private SecurityWebFiltersOrder order = SecurityWebFiltersOrder.AUTHENTICATION;

	public WebFilterPosition(WebFilter webFilter, OtherWebFilter position, SecurityWebFiltersOrder order) {
		this.webFilter = webFilter;
		this.position = position;
		this.order = order;
	}

	public WebFilter getWebFilter() {
		return webFilter;
	}

	public OtherWebFilter getPosition() {
		return position;
	}

	public SecurityWebFiltersOrder getOrder() {
		return order;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private WebFilter webFilter;
		private OtherWebFilter position = OtherWebFilter.AT;
		private SecurityWebFiltersOrder order = SecurityWebFiltersOrder.AUTHENTICATION;

		private Builder() {
		}

		public static Builder aWebFilterPosition() {
			return new Builder();
		}

		public Builder setWebFilter(WebFilter webFilter) {
			this.webFilter = webFilter;
			return this;
		}

		public Builder setPosition(OtherWebFilter position) {
			this.position = position;
			return this;
		}

		public Builder setOrder(SecurityWebFiltersOrder order) {
			this.order = order;
			return this;
		}

		public WebFilterPosition build() {
			return new WebFilterPosition(webFilter, position, order);
		}
	}
}
