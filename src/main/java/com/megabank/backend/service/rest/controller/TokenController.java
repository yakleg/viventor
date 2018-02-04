package com.megabank.backend.service.rest.controller;


import com.megabank.backend.service.security.annotation.AnonymousAccess;
import com.megabank.backend.service.security.component.SecurityTokenBean;
import com.megabank.backend.service.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.megabank.backend.service.security.api.TokenManager.TOKEN_HEADER;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/api/token")
public class TokenController {

	private SecurityTokenBean tokenService;

	@Autowired
	public void setTokenService(SecurityTokenBean tokenService) {
		this.tokenService = tokenService;
	}

	@AnonymousAccess
	@PostMapping(consumes = APPLICATION_JSON_VALUE)
	public String issue(@RequestBody User user) {
		UUID token = tokenService.issueToken(user.getEmail(), user.getPassword());
		return token.toString();
	}

	@DeleteMapping
	public void revoke(@RequestHeader(TOKEN_HEADER) UUID token) {
		tokenService.revoke(token);
	}
}
