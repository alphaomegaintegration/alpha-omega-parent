package com.alpha.omega.security.config;


import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
//@Import({AOSecurityReactiveConfig.class})
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
public @interface EnableAOSecurityReactive {
}
