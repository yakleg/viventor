package com.megabank.backend.service.user.component;

import com.megabank.backend.service.user.api.UserManager;
import com.megabank.backend.service.user.dao.UserRepository;
import com.megabank.backend.service.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.AfterDeleteEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.core.event.BeforeDeleteEvent;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

@Component
public class UserManagerBean implements UserManager {

	private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private ApplicationEventPublisher eventPublisher;

	private UserRepository userRepository;

	@Autowired
	public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	@Autowired
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Optional<User> find(int userId) {
		log.info("Getting user #{}", userId);

		return Optional.ofNullable(userRepository.findOne(userId));
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User create(User user) {
		log.info("Creating new user");

		eventPublisher.publishEvent(new BeforeCreateEvent(user));
		userRepository.save(user);
		eventPublisher.publishEvent(new AfterCreateEvent(user));
		return user;
	}

	@Override
	public void delete(int userId) {
		log.info("Deleting user #{}", userId);

		User user = userRepository.findOne(userId);
		eventPublisher.publishEvent(new BeforeDeleteEvent(user));
		userRepository.delete(user);
		eventPublisher.publishEvent(new AfterDeleteEvent(user));
	}
}
