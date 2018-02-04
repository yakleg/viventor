package com.megabank.backend.service.account.api;

import com.megabank.backend.service.account.domain.Account;
import com.megabank.backend.service.account.domain.Posting;

import java.math.BigDecimal;
import java.util.List;

public interface AccountManager {

	List<Account> findAll(int userId);

	Account addAccount(int userId, Account account);

	void delete(int accountId);

	Posting addPosting(int accountId, Posting posting);

	BigDecimal getBalance(int accountId);

	List<Posting> getStatement(int accountId);
}
