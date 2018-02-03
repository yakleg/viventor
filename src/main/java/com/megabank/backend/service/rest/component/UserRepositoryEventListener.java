package com.megabank.backend.service.rest.component;

import com.megabank.backend.service.dao.AccountRepository;
import com.megabank.backend.service.dao.PostingRepository;
import com.megabank.backend.service.domain.Account;
import com.megabank.backend.service.domain.User;
import com.megabank.backend.service.rest.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.Currency;

import static java.math.BigDecimal.ZERO;

@Component
public class UserRepositoryEventListener extends AbstractRepositoryEventListener<User> {

	private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final AccountRepository accountRepository;

	private final PostingRepository postingRepository;

	@Autowired
	public UserRepositoryEventListener(AccountRepository accountRepository, PostingRepository postingRepository) {
		this.accountRepository = accountRepository;
		this.postingRepository = postingRepository;
	}

	@Override
	protected void onAfterCreate(User user) {
		// Creating default account for new users
		Account account = new Account();
		account.setName("Default");
		account.setUser(user);
		account.setCurrency(Currency.getInstance("USD")); // TaDa hardcode...
		log.info("Creating \"Default\" account for user #{}", user.getId());
		accountRepository.save(account);
	}

	@Override
	protected void onBeforeDelete(User user) {
		log.info("Checking for positive balance before user #{} before deletion", user.getId());
		BigDecimal amount = postingRepository.sumOfPostingsByUserId(user.getId()).orElse(ZERO);

		if (ZERO.compareTo(amount) != 0) {
			log.error("Can't delete user #{} with positive balance", user.getId());
			throw new BusinessException("Can't delete user with positive balance.");
		}
	}
}
