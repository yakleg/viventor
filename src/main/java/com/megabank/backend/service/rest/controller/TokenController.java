package com.megabank.backend.service.rest.controller;


import com.megabank.backend.service.security.component.SecurityTokenBean;
import com.megabank.backend.service.dao.UserRepository;
import com.megabank.backend.service.domain.User;
import com.megabank.backend.service.rest.exception.RestAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/api/token")
public class TokenController {

	private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private SecurityTokenBean tokenService;

	@Autowired
	private UserRepository userRepository;

	@PostMapping(consumes = APPLICATION_JSON_VALUE)
	public String issue(@RequestBody User user) {
		log.info("Issuing new token");
		user = userRepository.findByEmailAndPassword(user.getEmail(), user.getPassword()).orElseThrow(() -> {
			log.error("Specified pair of email and password is not found");
			return new RestAuthenticationException("User name and password are not defined.");
		});

		return tokenService.issueToken(user).toString();
	}

	@DeleteMapping
	public void revoke(@RequestHeader("X-Auth-Token") UUID token) {
		tokenService.revoke(token);
		log.info("Token {} is revoked", token);
	}
}
