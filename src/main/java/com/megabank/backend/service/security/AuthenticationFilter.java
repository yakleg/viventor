package com.megabank.backend.service.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.megabank.backend.service.security.api.TokenManager.TOKEN_HEADER;
import static org.springframework.http.HttpStatus.FORBIDDEN;

public class AuthenticationFilter extends OncePerRequestFilter {

	private final AuthenticationManager authenticationManager;

	public AuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		Optional<String> tokenOptional = Optional.ofNullable(request.getHeader(TOKEN_HEADER));

		if (!tokenOptional.isPresent()) {
			filterChain.doFilter(request, response);
			return;
		}

		UUID token = UUID.fromString(tokenOptional.get());
		try {
			Authentication authentication = authenticationManager.authenticate(new PreAuthenticatedAuthenticationToken(token, null));
			if (authentication != null && authentication.isAuthenticated()) {
				SecurityContextHolder.getContext().setAuthentication(authentication);
				filterChain.doFilter(request, response);
			} else {
				response.sendError(FORBIDDEN.value(), FORBIDDEN.getReasonPhrase());
			}
		} catch (AuthenticationException e) {
			response.sendError(FORBIDDEN.value(), FORBIDDEN.getReasonPhrase());
		}

	}
}
