package com.alpha.omega.security.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.util.SimpleMethodInvocation;
import org.springframework.web.context.ContextLoader;

import java.lang.reflect.Method;

public class AOSecurityChecker {

	private static Logger logger = LogManager.getLogger(AOSecurityChecker.class);


	private static class SecurityObject{
		public void triggerCheck(){ /*NOP*/ }
	}

	private static Method triggerCheckMethod;
	private static SpelExpressionParser parser;

	static{
		try{ triggerCheckMethod =  SecurityObject.class.getMethod("triggerCheck"); }
		catch (NoSuchMethodException e) { logger.error(e); }
		parser = new SpelExpressionParser();
	}

	public static boolean check(String securityExpression){
		if (logger.isDebugEnabled()) { logger.debug("Checking security expression ["+securityExpression+"]..."); }
		boolean checkResult = Boolean.FALSE;
		if (ContextLoader.getCurrentWebApplicationContext() != null){
			SecurityObject securityObject = new SecurityObject();
			MethodSecurityExpressionHandler expressionHandler = ContextLoader.getCurrentWebApplicationContext().getBean(DefaultMethodSecurityExpressionHandler.class);
			if (expressionHandler != null){
				EvaluationContext evaluationContext = expressionHandler.createEvaluationContext(SecurityContextHolder.getContext().getAuthentication(), new SimpleMethodInvocation(securityObject, triggerCheckMethod));
				checkResult = ExpressionUtils.evaluateAsBoolean(parser.parseExpression(securityExpression), evaluationContext);
			}

		}

		if (logger.isDebugEnabled()){ logger.debug("Check result: "+checkResult); }

		return checkResult;
	}
}
