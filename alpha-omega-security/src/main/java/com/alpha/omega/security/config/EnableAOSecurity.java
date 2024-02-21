package com.alpha.omega.security.config;

import com.enterprise.pwc.datalabs.security.filter.PwcSecurityFilterConfig;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({PwcSecurityFilterConfig.class})
@EnableWebSecurity
public @interface EnableAOSecurity {
}
