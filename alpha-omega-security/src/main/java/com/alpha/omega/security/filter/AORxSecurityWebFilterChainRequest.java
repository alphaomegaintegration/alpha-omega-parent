package com.alpha.omega.security.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.reactive.resource.ResourceUrlProvider;
import org.springframework.web.server.ServerWebExchange;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class AORxSecurityWebFilterChainRequest {

	private Set<String> protectedUrls = new HashSet<>();
	private Map<String, HttpMethod> protectedUrlsMethod = new LinkedHashMap<>();
	private List<String> excludeUrls = new ArrayList<>();
	private ServerHttpSecurity httpSecurity;
	private List<WebFilterPosition> webFilters = new ArrayList<>();
	private ReactiveAuthenticationManagerResolver<ServerWebExchange> resolver;
	private ServerAuthenticationConverter converter;
	private String loggableRequestHeadersStr;
	private boolean disableCSRF = Boolean.TRUE;
	private ResourceUrlProvider resourceUrlProvider;

}
