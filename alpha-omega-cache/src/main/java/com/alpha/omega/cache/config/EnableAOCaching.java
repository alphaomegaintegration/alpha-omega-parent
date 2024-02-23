package com.alpha.omega.cache.config;

import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan(basePackages = "com.alpha.omega.cache.config")
public @interface EnableAOCaching {
}
