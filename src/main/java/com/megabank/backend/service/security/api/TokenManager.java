package com.megabank.backend.service.security.api;

import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface TokenManager {

	String TOKEN_HEADER = "X-Auth-Token";

	UUID issueToken(String email, String password);

	Authentication findActor(UUID token);

	void revoke(UUID token);
}
