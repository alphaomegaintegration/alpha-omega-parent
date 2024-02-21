package com.alpha.omega.cache.instant;

import java.time.Instant;

public class StringInstant implements CacheInstant{

	Instant instant;
	String str;

	public StringInstant(Instant instant, String str) {
		this.instant = instant;
		this.str = str;
	}

	public StringInstant(String str) {
		this.str = str;
		this.instant = Instant.now();
	}

	@Override
	public String toString() {
		return str;
	}

	@Override
	public Instant getInstant() {
		return instant;
	}
}
