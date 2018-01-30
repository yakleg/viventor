package com.megabank.backend.service.security.component;

import com.megabank.backend.service.domain.User;
import com.megabank.backend.service.security.api.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SecurityTokenBean implements TokenManager {

	@Autowired
	private CacheManager cacheManager;

	private Cache getCache() {
		return cacheManager.getCache("token");
	}

	@Override
	public UUID issueToken(User user) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				user.getEmail(),
				user.getPassword(),
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
	}
}
