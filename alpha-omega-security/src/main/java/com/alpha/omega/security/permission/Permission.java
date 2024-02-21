package com.alpha.omega.security.permission;

public class Permission {

	enum AccessControl {
		ALL(7),READ(4), WRITE(2), ENABLED(1);

		final int access;

		AccessControl(int access) {
			this.access = access;
		}
	}

	String name;
	String description;
	String contextId;

	AccessControl accessControl;
	boolean optional;
	boolean enabled = Boolean.TRUE.booleanValue();
}
