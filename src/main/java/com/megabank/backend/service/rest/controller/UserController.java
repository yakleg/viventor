package com.megabank.backend.service.rest.controller;

import com.megabank.backend.service.security.annotation.AnonymousAccess;
import com.megabank.backend.service.user.api.UserManager;
import com.megabank.backend.service.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/users", produces = APPLICATION_JSON_VALUE)
public class UserController {

	private UserManager userManager;

	@Autowired
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	@GetMapping("/{userId}")
	public User get(@PathVariable int userId) {
		return userManager.find(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found."));
	}

	@AnonymousAccess
	@PostMapping(consumes = APPLICATION_JSON_VALUE)
	public User create(@RequestBody User user) {
		return userManager.create(user);
	}

	@DeleteMapping("/{userId}")
	public void delete(@PathVariable int userId) {
		userManager.delete(userId);
	}
}
