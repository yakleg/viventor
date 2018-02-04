package com.megabank.backend.service.account.component;

import com.megabank.backend.service.account.api.AccountManager;
import com.megabank.backend.service.account.dao.AccountRepository;
import com.megabank.backend.service.account.dao.PostingRepository;
import com.megabank.backend.service.account.domain.Account;
import com.megabank.backend.service.account.domain.Posting;
import com.megabank.backend.exception.BusinessException;
import com.megabank.backend.service.user.api.UserManager;
import com.megabank.backend.service.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.AfterDeleteEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.core.event.BeforeDeleteEvent;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;

@Component
@RepositoryEventHandler
public class AccountManagerBean implements AccountManager {

	private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private ApplicationEventPublisher eventPublisher;

	private UserManager userManager;

	private AccountRepository accountRepository;

	private PostingRepository postingRepository;

	@Autowired
	public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	@Autowired
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	@Autowired
	public void setAccountRepository(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Autowired
	public void setPostingRepository(PostingRepository postingRepository) {
		this.postingRepository = postingRepository;
	}

	@Override
	public List<Account> findAll(int userId) {
		return accountRepository.findAllByUserId(userId);
	}

	@Override
	public Account addAccount(int userId, Account account) {
		log.info("Creating account for user #{} with name \"{}\"", userId, account.getName());

		// Link new account with user
		User user = userManager.find(userId).orElseThrow(() -> new BusinessException("User not found."));
		account.setUser(user);

		eventPublisher.publishEvent(new BeforeCreateEvent(account));
		accountRepository.save(account);
		eventPublisher.publishEvent(new AfterDeleteEvent(account));

		return account;
	}

	@Override
	public void delete(int accountId) {
		Account account = accountRepository.findOne(accountId);
		log.info("Deleting account for user #{} with name \"{}\"", account.getUser().getId(), account.getName());

		eventPublisher.publishEvent(new BeforeDeleteEvent(account));
		accountRepository.delete(account);
		eventPublisher.publishEvent(new AfterDeleteEvent(account));
	}

	@Override
	public Posting addPosting(int accountId, Posting posting) {
		Optional<Account> account = Optional.ofNullable(
				accountRepository.findOne(accountId)
		);

		// Linking posting with account if it exist
		posting.setAccount(account.orElseThrow(() -> new BusinessException("Account not found.")));

		eventPublisher.publishEvent(new BeforeCreateEvent(posting));
		postingRepository.save(posting);
		eventPublisher.publishEvent(new AfterCreateEvent(posting));

		return posting;
	}

	@Override
	public BigDecimal getBalance(int accountId) {
		return postingRepository.sumOfPostingsByAccountId(accountId).orElse(ZERO);
	}

	@Override
	public List<Posting> getStatement(int accountId) {
		return postingRepository.findAllByAccountId(accountId);
	}
}
