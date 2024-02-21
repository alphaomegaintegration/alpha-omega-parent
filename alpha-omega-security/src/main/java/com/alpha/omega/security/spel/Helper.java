package com.alpha.omega.security.spel;

import com.alpha.omega.security.permission.AOSimpleAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;

import java.util.Arrays;

public class Helper {
	private static final Logger logger = LoggerFactory.getLogger(Helper.class);

	public static boolean hasPermission(Authentication authentication, Object permission) {
		logger.debug("hasPermission authentication => {}, permission => {}", new Object[]{authentication, permission});
		return authentication.getAuthorities().contains(new AOSimpleAuthority(permission.toString()));
	}

	public static boolean hasPermissions(Authentication authentication, Object... permissions) {
		//logger.debug("hasPermissions authentication => {}, permissions => {}", new Object[]{authentication, permissions});
		return Arrays.asList(permissions).stream()
				.filter(perm -> hasPermission(authentication,perm))
				.map(obj -> Boolean.TRUE)
				.findFirst().orElse(Boolean.FALSE);

	}

	public boolean hasAuthorities(Authentication authentication, Object... permissions){
		return hasPermissions(authentication, permissions);
	}

	public boolean hasAuthority(Authentication authentication, Object permission){
		return hasPermission(authentication, permission);
	}

	@Configuration
	public static class ConfigHolder{

		@Autowired
		public Environment environment;

	}


}
