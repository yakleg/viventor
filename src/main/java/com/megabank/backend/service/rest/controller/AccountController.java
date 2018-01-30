package com.megabank.backend.service.rest.controller;


import com.megabank.backend.service.dao.AccountRepository;
import com.megabank.backend.service.dao.PostingRepository;
import com.megabank.backend.service.dao.UserRepository;
import com.megabank.backend.service.domain.Account;
import com.megabank.backend.service.domain.User;
import com.megabank.backend.service.rest.dto.Balance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.event.BeforeDeleteEvent;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(path = "/api/users/{userId}/accounts")
public class AccountController {

	private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PostingRepository postingRepository;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@GetMapping
	public List<Account> list(@PathVariable long userId) {
		return accountRepository.findAllByUserId(userId);
	}

	@PostMapping
	public Account create(@PathVariable long userId, @RequestBody Account account) {
		log.info("Creating account for user #{} with name \"{}\"", userId, account.getName());

		// Link new account with user
		User user = userRepository.getOne(userId);
		account.setUser(user);

		return accountRepository.save(account);
	}

	@DeleteMapping("/{accountId}")
	public void delete(@PathVariable long userId, @PathVariable long accountId) {
		log.info("Deleting account #{} of user #{}", accountId, userId);

		checkExists(accountId);
		Account account = accountRepository.findOne(accountId);
		eventPublisher.publishEvent(new BeforeDeleteEvent(account));
		accountRepository.delete(accountId);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{accountId}/balance")
	public Balance balance(@PathVariable long accountId) {
		checkExists(accountId);
		BigDecimal amount = postingRepository.sumOfPostingsByAccountId(accountId);
		if (amount == null) {
			amount = BigDecimal.ZERO;
		}
		return new Balance(amount);
	}

	private void checkExists(long accountId) {
		if (!accountRepository.exists(accountId)) {
			throw new ResourceNotFoundException("Account not found.");
		}
	}
}
