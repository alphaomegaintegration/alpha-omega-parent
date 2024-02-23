package com.alpha.omega.security.permission;

import org.apache.logging.log4j.LogManager;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

public class AOPermissionEvaluator implements PermissionEvaluator {

	private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(AOPermissionEvaluator.class);

	public AOPermissionEvaluator() {
	}

	public boolean hasPermission(Authentication authentication, Object permission) {
		boolean hasPermission = authentication.getAuthorities().contains(new AOSimpleAuthority(permission.toString()));
		logger.debug("hasPermission => {} authentication => {}, permission => {}",
				new Object[]{hasPermission, authentication, permission});
		return hasPermission;
	}

	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		boolean hasPermission = authentication.getAuthorities().contains(new AOSimpleAuthority(permission.toString()));
		logger.debug("hasPermission => {} authentication => {}, permission => {} targetDomainObject => {}",
				new Object[]{hasPermission,authentication, permission, targetDomainObject});
		return hasPermission;
	}

	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		boolean hasPermission = authentication.getAuthorities().contains(new AOSimpleAuthority(permission.toString()));
		logger.debug("hasPermission => {} with targetId authentication => {}, permission => {} targetId => {}, targetType => {}",
				new Object[]{hasPermission,authentication, permission, targetId, targetType});
		return hasPermission;
	}
}
