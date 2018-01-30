package com.megabank.backend.service.security;

import com.megabank.backend.service.security.api.TokenManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.UUID;

public class TokenAuthenticationProvider implements AuthenticationProvider {

	private final TokenManager tokenManager;

	public TokenAuthenticationProvider(TokenManager tokenManager) {
		this.tokenManager = tokenManager;
	}

	@Override
	public Authentication authenticate(Authentication token) throws AuthenticationException {
		return tokenManager.findActor((UUID) token.getPrincipal());
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return aClass.equals(PreAuthenticatedAuthenticationToken.class);
	}
}