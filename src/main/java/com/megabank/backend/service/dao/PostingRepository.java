package com.megabank.backend.service.dao;

import com.megabank.backend.service.domain.Posting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PostingRepository extends JpaRepository<Posting, Long> {

	@Query("select p from Posting p join p.account as a where a.id = :accountId and a.user.id = :userId")
	List<Posting> findAllByAccountIdAndUserId(@Param("userId") long userId, @Param("accountId") long accountId);

	@Query("select sum(p.amount) from Posting p join p.account as a where a.user.id = :userId")
	BigDecimal sumOfPostingsByUserId(@Param("userId") long userId);

	@Query("select sum(p.amount) from Posting p join p.account as a where a.id = :accountId")
	BigDecimal sumOfPostingsByAccountId(@Param("accountId") long accountId);
}
