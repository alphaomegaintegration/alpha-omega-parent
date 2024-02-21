package com.alpha.omega.cache;

/*
https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control
 */

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum CacheControl {
    NONE("no-cache"),
    REFRESH("must-revalidate"),
    USE("");

    String CacheControlHeader;

	public String getCacheControlHeader() {
		return CacheControlHeader;
	}

	CacheControl(String cacheControlHeader) {
        CacheControlHeader = cacheControlHeader;
    }

	public static CacheControl of(String cacheControl){

		if (StringUtils.isBlank(cacheControl)){
			return CacheControl.USE;
		}

		Optional<CacheControl> cacheControlOptional = Arrays.stream(CacheControl.values())
				.filter(cc -> cc.getCacheControlHeader().equals(cacheControl))
				.findFirst();

		if (cacheControlOptional.isPresent()){
			return cacheControlOptional.get();
		} else {
			return NONE;
		}
	}
}
