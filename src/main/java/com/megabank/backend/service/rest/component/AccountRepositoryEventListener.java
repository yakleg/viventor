package com.megabank.backend.service.rest.component;

import com.megabank.backend.service.dao.AccountRepository;
import com.megabank.backend.service.domain.Account;
import com.megabank.backend.service.rest.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class AccountRepositoryEventListener extends AbstractRepositoryEventListener<Account> {

	private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final AccountRepository accountRepository;

	@Autowired
	public AccountRepositoryEventListener(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	protected void onBeforeDelete(Account account) {
		if (accountRepository.findAllByUserId(account.getUser().getId()).size() == 1) {
			log.error("Can't delete last account #{} of user #{}", account.getId(), account.getUser().getId());
			throw new BusinessException("Can't delete last account.");
		}
	}
}
