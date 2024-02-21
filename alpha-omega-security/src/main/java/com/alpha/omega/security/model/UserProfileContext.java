package com.alpha.omega.security.model;

public class UserProfileContext {
	private static ThreadLocal<UserProfile> threadLocal = new ThreadLocal<>();
	public static ThreadLocal<UserProfile> getThreadLocal() {
		return threadLocal;
	}
}
