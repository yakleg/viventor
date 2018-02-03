package com.megabank.backend.service.dao;

import com.megabank.backend.service.domain.Posting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PostingRepository extends JpaRepository<Posting, Integer> {

	@Query("select p from Posting p join p.account as a where a.id = :accountId and a.user.id = :userId")
	List<Posting> findAllByAccountIdAndUserId(@Param("userId") int userId, @Param("accountId") int accountId);

	@Query("select sum(p.amount) from Posting p join p.account as a where a.user.id = :userId")
	Optional<BigDecimal> sumOfPostingsByUserId(@Param("userId") int userId);

	@Query("select sum(p.amount) from Posting p join p.account as a where a.id = :accountId")
	Optional<BigDecimal> sumOfPostingsByAccountId(@Param("accountId") int accountId);
}
