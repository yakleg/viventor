package com.megabank.backend.service.security.api;

import com.megabank.backend.service.domain.User;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface TokenManager {
	UUID issueToken(User user);
	Authentication findActor(UUID token);
	void revoke(UUID token);
}
