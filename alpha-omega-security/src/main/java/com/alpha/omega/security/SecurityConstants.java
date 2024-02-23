package com.alpha.omega.security;

import java.util.function.Function;

public class SecurityConstants {

    public final static String BEARER_STARTS_WITH = "Bearer ";
    public final static Function<String, Boolean> BEARER_STARTS_WITH_FUNCTION = (auth) -> BEARER_STARTS_WITH.startsWith(auth);

    public final static String CLIENT_REGISTRATION_KEY_PREFIX = "clientRegistrationEntity";
}
