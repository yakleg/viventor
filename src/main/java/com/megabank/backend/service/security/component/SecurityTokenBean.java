package com.megabank.backend.service.security.component;

import com.megabank.backend.exception.AuthenticationException;
import com.megabank.backend.service.user.api.UserManager;
import com.megabank.backend.service.user.domain.User;
import com.megabank.backend.service.security.api.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.UUID;

@Service
public class SecurityTokenBean implements TokenManager {

	private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private CacheManager cacheManager;

	private UserManager userManager;

	@Autowired
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Autowired
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	private Cache getCache() {
		return cacheManager.getCache("token");
	}

	@Override
	public UUID issueToken(String email, String password) {
		Objects.requireNonNull(email, "Email can't be null");
		Objects.requireNonNull(password, "Password can't be null");
		log.info("Issuing new token");

		User user = userManager.findByEmail(email)
				.filter(usr -> password.equals(usr.getPassword()))
				.orElseThrow(() -> {
					log.error("Specified pair of email and password is not found");
					return new AuthenticationException("User name and password are not defined.");
				});


		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				user.getEmail(), user.getPassword(),
				AuthorityUtils.createAuthorityList("ROLE_USER")); // TaDa hardcode...

		authentication.setDetails(user);

		UUID token = UUID.randomUUID();
		getCache().put(token, authentication);
		return token;
	}

	@Override
	public Authentication findActor(UUID token) {
		return getCache().get(token, Authentication.class);
	}

	@Override
	public void revoke(UUID token) {
		getCache().evict(token);
		log.info("Token {} is revoked", token);

	}
}
