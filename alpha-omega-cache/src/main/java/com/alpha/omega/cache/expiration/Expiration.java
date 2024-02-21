package com.alpha.omega.cache.expiration;

import java.time.Instant;
import java.util.Date;

/**
 * {@code Expiration} has resolution to one second, although a milliseconds
 * factory constructor is provided for convenience.
 *
 */
public final class Expiration {

	private boolean isOffset;
	private long millis;

	/**
	 * Creates an expiration at specific date/time.
	 *
	 * @param expirationTime date/time after which an item must be discarded
	 *    from the cache.
	 * @return a new {@code Expiration} object representing the given
	 *    {@link Date}.
	 */
	public static Expiration onDate(Date expirationTime) {
		return new Expiration(expirationTime.getTime(), false);
	}

	/**
	 * Creates an {@code Expiration} for some number of milliseconds into the
	 * future.
	 *
	 * @param milliDelay amount of time, in milliseconds, after which an item
	 *    must be discarded from the cache.
	 * @return a new {@code Expiration} representing the requested time.
	 */
	public static Expiration byDeltaMillis(int milliDelay) {
		return new Expiration(milliDelay, true);
	}
	/**
	 * Creates an {@code Expiration} for some number of milliseconds into the
	 * future.
	 *
	 * @param milliDelay amount of time, in milliseconds, after which an item
	 *    must be discarded from the cache.
	 * @return a new {@code Expiration} representing the requested time.
	 */
	public static Expiration byDeltaMillis(long milliDelay) {
		return new Expiration(milliDelay, true);
	}


	public static Expiration byDeltaInstant(Instant milliDelay) {
		return new Expiration(milliDelay.toEpochMilli(), true);
	}

	public Instant toInstant(){
		if (isOffset) {
			return Instant.ofEpochMilli(System.currentTimeMillis() + millis);
		} else {
			return Instant.ofEpochMilli(millis);
		}
	}

	/**
	 * Creates an {@code Expiration} for some number of
	 * seconds in the future.
	 * @param secondsDelay number of seconds after which an item must be
	 *   discarded.
	 * @return a new {@code Expiration} for the requested time.
	 */
	public static Expiration byDeltaSeconds(int secondsDelay) {
		return byDeltaMillis(secondsDelay * 1000);
	}

	/**
	 * Creates an {@code Expiration} for some number of
	 * seconds in the future.
	 * @param secondsDelay number of seconds after which an item must be
	 *   discarded.
	 * @return a new {@code Expiration} for the requested time.
	 */
	public static Expiration byDeltaSeconds(long secondsDelay) {
		return byDeltaMillis(secondsDelay * 1000);
	}

	private Expiration(long millis, boolean isOffset) {
		this.millis = millis;
		this.isOffset = isOffset;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Expiration) {
			return ((Expiration) obj).millis == millis &&
					((Expiration) obj).isOffset == isOffset;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (int) millis;
	}

	/**
	 * Fetches the expiration date, in milliseconds-since-epoch.
	 *
	 * @return timestamp of expiration, even if constructed via a delta
	 *     rather than fixed date.
	 */
	public long getMillisecondsValue() {
		if (isOffset) {
			return System.currentTimeMillis() + millis;
		} else {
			return millis;
		}
	}

	/**
	 * Fetches the expiration date, in seconds-since-epoch.
	 *
	 * @return timestamp of expiration, even if constructed via a delta
	 *     rather than fixed date.
	 */
	public int getSecondsValue() {
		return (int) (getMillisecondsValue() / 1000);
	}
}
