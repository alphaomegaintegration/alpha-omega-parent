package com.pwc.base.model;

public class UserProfileContext {
	private static ThreadLocal<UserProfile> threadLocal = new ThreadLocal<>();
	public static ThreadLocal<UserProfile> getThreadLocal() {
		return threadLocal;
	}
}
