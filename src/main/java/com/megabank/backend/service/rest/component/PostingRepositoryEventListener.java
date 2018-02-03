package com.megabank.backend.service.rest.component;

import com.megabank.backend.service.dao.PostingRepository;
import com.megabank.backend.service.domain.Posting;
import com.megabank.backend.service.rest.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

@Component
public class PostingRepositoryEventListener extends AbstractRepositoryEventListener<Posting> {

	private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final PostingRepository postingRepository;

	@Autowired
	public PostingRepositoryEventListener(PostingRepository postingRepository) {
		this.postingRepository = postingRepository;
	}

	@Override
	protected void onBeforeCreate(Posting posting) {
		if (ZERO.compareTo(posting.getAmount()) < 0) {
			return;
		}
		// Checking of excess balance if new posting amount less than 0
		BigDecimal amount = postingRepository.sumOfPostingsByAccountId(posting.getAccount().getId()).orElse(ZERO);

		if (ZERO.compareTo(amount.add(posting.getAmount())) > 0) {
			log.error("Insufficient funds on account #{}", posting.getAccount().getId());
			throw new BusinessException("Insufficient funds on account.");
		}

	}
}
