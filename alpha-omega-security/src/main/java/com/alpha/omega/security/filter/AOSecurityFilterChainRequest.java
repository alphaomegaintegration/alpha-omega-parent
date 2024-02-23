package com.alpha.omega.security.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.Filter;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

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
public class AOSecurityFilterChainRequest {

	private List<Filter> filters = new ArrayList<>();
	private Map<String, HttpMethod> protectedUrlsMethod = new LinkedHashMap<>();
	private Set<String> protectedUrls = new LinkedHashSet<>();
	private List<String> excludeUrls = new ArrayList<>();
	private HttpSecurity httpSecurity;
	private boolean disableCSRF = Boolean.TRUE;

}
