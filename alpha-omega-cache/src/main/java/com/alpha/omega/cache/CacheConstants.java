package com.alpha.omega.cache;

import com.alpha.omega.cache.expiration.Expiration;

public final class CacheConstants {

	private CacheConstants(){}

	public static final int SECOND = 1;
	public static final int SECONDS_IN_MINUTE = 60;
	public static final int MINUTES_IN_HOUR = 60;
	public static final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR;
	public static final int HOURS_IN_DAY = 24;
	public static final int ONE_DAY_AS_SECONDS = SECONDS_IN_HOUR * HOURS_IN_DAY;

	public static final Expiration EXPIRES_24_HOURS = Expiration.byDeltaSeconds(ONE_DAY_AS_SECONDS);

	public static final String CACHE_NAMESPACE_TOKEN = "TOKEN";
	public static final String CACHE_NAMESPACE_USER = "USER";

}
