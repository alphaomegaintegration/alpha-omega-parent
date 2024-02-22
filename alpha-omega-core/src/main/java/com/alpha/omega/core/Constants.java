package com.alpha.omega.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Constants {
    public static final String CLIENT_ID_HEADER = "clientId";
    public static final String CORRELATION_ID = "correlationId";
    public static final String CONTEXT_ID = "contextId";
    public static final String IDENTITY_PROVIDER = "identityProvider";
    public static final String REFRESH_TOKEN_HEADER = "refreshToken";
    public static final String ALERT = "ALERT :: ";
    public static final String RAISE_ALARM_KEY = "raiseAlarm";
    public static final String ALERT_KEY = "alert";
    public static final String NOT_AVAILABLE = "NA";

    // Storage Account Types
    public static final String STORAGE_ACCOUNT_TYPE = "storageAccountType";
    public static final String TRUSTED_ZONE = "TRUSTED_ZONE";
    public static final String TRUSTED_ZONE_ADLSG2 = "TRUSTED_ZONE_ADLSG2";
    public static final String LANDING_ZONE = "LANDING_ZONE";
    public static final String DROP_ZONE = "DROP_ZONE";

    // Cluster Types
    public static final String INTERACTIVE = "INTERACTIVE";
    public static final String ENTERPRISE = "ENTERPRISE";
    public static final String CORE = "CORE";
    public static final String OPS = "OPS";
    public static final String COMPUTE = "COMPUTE";
    public static final String EPHEMERAL = "EPHEMERAL";
    public static final String HDP = "HDP";
    public static final String HDF = "HDF";

    public static final String EVENT_TYPE = "eventType";
    public static final String EVENT_SUCCESS = ".SUCCESS";
    public static final String EVENT_FAILED = ".FAILED";
    public static final String KEY = "key";



    // REST Related
    public static final String HTTPS = "https://";
    public static final String HTTP = "http://";
    public static final String HEALTH_ENDPOINT = "/actuator/health";
    public static final String INFO_ENDPOINT = "/actuator/info";
    public static final String SPRING_APPLICATION_NAME = "spring.application.name";
    public static final String SWAGGER_BASE_PATH = "SWAGGER_BASE_PATH";
    public static final String UP = "UP";
    public static final String DOWN = "DOWN";
    public static final String BEARER = "Bearer ";
    public static final String BASIC = "Basic ";
    public static final String SIZE = "size";
    public static final String ID_TOKEN = "id_token";
    public static final String EXPIRES_IN = "expires_in";
    public static final String PRINCIPAL = "Principal";
    public static final String USERNAME = "username";
    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String OAUTH_GRANT_TYPE = "grant_type";
    public static final String OAUTH_CLIENT_CREDENTIALS = "client_credentials";
    public static final String OAUTH_PWD_GRANT_TYPE = "password";
    public static final String OAUTH_CLIENT_ID = "client_id";
    public static final String OAUTH_RESOURCE = "resource";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String REFRESH_TOKEN_HEADER_RESPONSE = "Refresh-Token";
    public static final String SUBJECT = "subject";

    public static final String IDAM_PROVIDER = "idam";
    public static final String OAUTH_CLIENT_SECRET = "client_secret";
    public static final String OAUTH_CLIENT_SCOPE = "scope";
    public static final String EXTRA_ATTRIBUTES = "extraAttributes";
    public static final String OAUTH_AUTH_CHAIN = "auth_chain";
    public static final String EMPTY_STR = "";
    public static final String SPACE_STR = " ";
    public static final String COLON = ":";
    public static final String PERIOD = ".";

    public static final String COMMA = ",";
    public static final String SPACE = " ";
    public static final String STAR = "*";
    public static final String DASH = "-";
    public static final String QUESTION_MARK = "?";
    public final static char DOUBLE_QUOTES = '"';
    public static final String ADD_PROVIDER = "aad";

    public static final String[] DEFAULT_IGNORE_URLS_ARRAY = {
            "/v3/api-docs",
            "swagger",
            "/swagger/**",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html**",
            "/webjars/**",
            "/favicon.ico",
            "/actuator/health",
            "/actuator/info",
            "/resource",
            "/error",
            "/build/info"};


    public static final String GUID = "guid";

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String COUNTRY = "country";

    public static final String LINE_OF_SERVICE = "lineofservice";
    public static final String LOS = "LOS";

    public static final String MISSING_CREDENTIALS = "missing credentials";
    public static final String USER_NOT_FOUND_IN_IDAM = "user not found in idam";
    public static final String INVALID_USER_CREDENTIALS = "Invalid user credentials";

    public static final List<String> DEFAULT_IGNORE_URLS_LIST = new ArrayList<>(Arrays.asList(DEFAULT_IGNORE_URLS_ARRAY));

    public static final String DD_TRACE_ID_KEY = "dd.trace_id";
    public static final String DD_SPAN_ID_KEY = "dd.span_id";
    public static final String COMMIT_ID_KEY = "commit_id";

    public static final String ELAPSED_SECONDS_FORMAT = "%.3f seconds";

    public static Function<Long, String> elapsedSeconds = (elapsed) -> String.format(ELAPSED_SECONDS_FORMAT, elapsed.doubleValue());

}
