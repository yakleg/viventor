package com.megabank.backend.service.rest.controller;


import com.megabank.backend.service.account.api.AccountManager;
import com.megabank.backend.service.account.domain.Account;
import com.megabank.backend.service.account.domain.Posting;
import com.megabank.backend.service.rest.dto.Balance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/api/users/{userId}/accounts", produces = APPLICATION_JSON_VALUE)
public class AccountController {

	private AccountManager accountManager;

	@Autowired
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	@GetMapping
	public List<Account> list(@PathVariable int userId) {
		return accountManager.findAll(userId);
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE)
	public Account create(@PathVariable int userId, @RequestBody Account account) {
		return accountManager.addAccount(userId, account);
	}

	@DeleteMapping("/{accountId}")
	public void delete(@PathVariable int accountId) {
		accountManager.delete(accountId);
	}

	@GetMapping("/{accountId}/balance")
	public Balance balance(@PathVariable int accountId) {
		BigDecimal balance = accountManager.getBalance(accountId);
		return new Balance(balance);
	}

	@GetMapping("/{accountId}/postings")
	public List<Posting> getStatement(@PathVariable int accountId) {
		return accountManager.getStatement(accountId);
	}

	@PostMapping(path = "/{accountId}/postings", consumes = APPLICATION_JSON_VALUE)
	public Posting addPosting(@PathVariable int accountId, @RequestBody Posting posting) {
		return accountManager.addPosting(accountId, posting);
	}
}
