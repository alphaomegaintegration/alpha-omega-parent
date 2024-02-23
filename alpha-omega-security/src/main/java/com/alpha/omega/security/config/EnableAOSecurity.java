package com.alpha.omega.security.config;

import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
//@Import({AOSecurityFilterConfig.class})
@EnableWebSecurity
public @interface EnableAOSecurity {
}
