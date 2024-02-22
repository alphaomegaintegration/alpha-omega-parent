package com.alpha.omega.security.context;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class UserContextPermissions {
	protected String username;
	protected String userId;
	protected String contextId;
	protected Set<String> permissions = new HashSet<>();

}
