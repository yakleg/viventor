package com.megabank.backend.service.dao;

import com.megabank.backend.service.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

	List<Account> findAllByUserId(int userId);

	Optional<Account> findByUserIdAndId(int userId, int accountId);
}
