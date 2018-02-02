package com.megabank.backend.service.rest.controller;

import com.megabank.backend.service.dao.UserRepository;
import com.megabank.backend.service.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.BeforeDeleteEvent;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/users", produces = APPLICATION_JSON_VALUE)
public class UserController {

	private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@GetMapping("/{userId}")
	public User get(@PathVariable int userId) {
		checkExists(userId);
		return userRepository.findOne(userId);
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE)
	public User create(@RequestBody User user) {
		log.info("Creating new user");
		userRepository.save(user);
		eventPublisher.publishEvent(new AfterCreateEvent(user));
		return user;
	}

	@DeleteMapping("/{userId}")
	public void delete(@PathVariable int userId) {
		log.info("Deleting user #{}", userId);
		checkExists(userId);
		User user = userRepository.getOne(userId);
		eventPublisher.publishEvent(new BeforeDeleteEvent(user));
		userRepository.delete(user);
	}

	private void checkExists(int userId) {
		if (!userRepository.exists(userId)) {
			log.error("User #{} not found", userId);
			throw new ResourceNotFoundException("User not found.");
		}
	}
}
