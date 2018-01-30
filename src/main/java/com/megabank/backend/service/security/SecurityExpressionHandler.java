package com.megabank.backend.service.security;

import com.megabank.backend.service.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;

import java.lang.invoke.MethodHandles;

public class SecurityExpressionHandler extends DefaultWebSecurityExpressionHandler {

	public static class RestResourceSecurityExpressionRoot extends WebSecurityExpressionRoot {

		private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

		RestResourceSecurityExpressionRoot(Authentication authentication, FilterInvocation filterInvocation) {
			super(authentication, filterInvocation);
		}

		public boolean isOwnerOfResource() {
			if (authentication instanceof UsernamePasswordAuthenticationToken) {
				User user = (User) authentication.getDetails();
				boolean result = request.getPathInfo().startsWith("/api/users/" + user.getId());
				if(!result) {
					log.error("Access denied for user #{}\t{} {}", user.getId(), request.getMethod(), request.getRequestURI());
				}
				return result;
			}
			return false;
		}
	}

	@Override
	protected SecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, FilterInvocation invocation) {
		RestResourceSecurityExpressionRoot root = new RestResourceSecurityExpressionRoot(authentication, invocation);
		root.setPermissionEvaluator(getPermissionEvaluator());
		root.setRoleHierarchy(getRoleHierarchy());
		return root;
	}
}
