package com.alpha.omega.security.client;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class SecurityUser implements UserDetails {


    @Builder.Default
    Collection<? extends GrantedAuthority> authorities = new HashSet<>();
    String password;
    String username;
    @Builder.Default
    boolean accountNonExpired = Boolean.TRUE;
    @Builder.Default
    boolean accountNonLocked = Boolean.TRUE;
    @Builder.Default
    boolean credentialsNonExpired = Boolean.TRUE;
    @Builder.Default
    boolean enabled = Boolean.TRUE;
    @Builder.Default
    Optional<Jwt> jwt = Optional.empty();

}
