package com.megabank.backend.service.dao;

import com.megabank.backend.service.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
	List<Account> findAllByUserId(int userId);
	Account findByUserIdAndId(int userId, int accountId);
}
