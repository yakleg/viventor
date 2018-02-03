package com.megabank.backend.service.rest.controller;

import com.megabank.backend.service.dao.AccountRepository;
import com.megabank.backend.service.dao.PostingRepository;
import com.megabank.backend.service.domain.Account;
import com.megabank.backend.service.domain.Posting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/api/users/{userId}/accounts/{accountId}/postings")
public class PostingController {

	private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PostingRepository postingRepository;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@GetMapping
	public List<Posting> list(@PathVariable int userId,
	                          @PathVariable int accountId) {
		return postingRepository.findAllByAccountIdAndUserId(userId, accountId);
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity create(@PathVariable int userId,
	                             @PathVariable int accountId,
	                             @RequestBody Posting posting) {
		Optional<Account> account = accountRepository.findByUserIdAndId(userId, accountId);
		// Linking posting with account if it exist
		posting.setAccount(account.orElseThrow(() -> {
			log.error("Account #{} of user #{} not found", accountId, userId);
			return new ResourceNotFoundException("Account not found.");
		}));

		eventPublisher.publishEvent(new BeforeCreateEvent(posting));
		postingRepository.save(posting);
		eventPublisher.publishEvent(new AfterCreateEvent(posting));

		return ResponseEntity.ok().build();
	}
}
