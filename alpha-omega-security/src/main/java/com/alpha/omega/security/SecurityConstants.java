package com.alpha.omega.security;

import java.util.function.Function;

public class SecurityConstants {

    public final static String BEARER_STARTS_WITH = "Bearer ";
    public final static Function<String, Boolean> BEARER_STARTS_WITH_FUNCTION = (auth) -> BEARER_STARTS_WITH.startsWith(auth);
    public static final String COLON = ":";
    public static final String COMMA = ",";
    public static final String STAR = "*";
    public static final String QUESTION_MARK = "?";
    public final static char DOUBLE_QUOTES = '"';
    public static final String CONTEXT_ID = "contextId";
    public static final String CORRELATION_ID = "correlationId";

    public final static String CLIENT_REGISTRATION_KEY_PREFIX = "clientRegistrationEntity";
}
