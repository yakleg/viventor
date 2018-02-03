package com.megabank.backend.configuration;

import com.megabank.backend.service.security.SecurityExpressionHandler;
import com.megabank.backend.service.security.api.TokenManager;
import com.megabank.backend.service.security.AuthenticationFilter;
import com.megabank.backend.service.security.TokenAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final TokenManager tokenManager;

	@Autowired
	public SecurityConfig(TokenManager tokenManager) {
		this.tokenManager = tokenManager;
	}

	private TokenAuthenticationProvider getTokenAuthenticationProvider() {
		return new TokenAuthenticationProvider(tokenManager);
	}

	private SecurityExpressionHandler getSecurityExpressionHandler() {
		return new SecurityExpressionHandler();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(getTokenAuthenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.csrf().disable()

				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

				.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/api/token", "/api/users").permitAll()
				//
				.antMatchers(HttpMethod.DELETE, "/api/token").hasRole("USER")
				//
				.antMatchers("/api/users/**").access("hasRole('USER') and isOwnerOfResource()")
				// register isOwnerOfResource expression handler
				.expressionHandler(getSecurityExpressionHandler());

		httpSecurity
				.addFilterBefore(new AuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class);
	}
}
