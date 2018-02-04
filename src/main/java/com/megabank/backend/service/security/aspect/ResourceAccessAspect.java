package com.megabank.backend.service.security.aspect;

import com.megabank.backend.exception.AuthenticationException;
import com.megabank.backend.service.account.domain.Account;
import com.megabank.backend.service.user.domain.User;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import javax.persistence.EntityManager;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;
import static org.springframework.web.servlet.HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;

@Aspect
@Component
public class ResourceAccessAspect {

	private EntityManager entityManager;

	@Autowired
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Before("!@annotation(com.megabank.backend.service.security.annotation.AnonymousAccess) && " +
			"execution(@org.springframework.web.bind.annotation.* * *(..))")
	public void checkResourceOwnership() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map<String, String> requestParams = (Map<String, String>) RequestContextHolder.currentRequestAttributes()
				.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE, SCOPE_REQUEST);

		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			User user = (User) authentication.getDetails();

			Optional<String> userIdOpt = Optional.ofNullable(requestParams.get("userId"));
			Optional<String> accountIdOpt = Optional.ofNullable(requestParams.get("accountId"));

			if (requestParams.size() > 0) {
				Integer userId = userIdOpt.map(Integer::valueOf).orElseThrow(() -> new AuthenticationException("Access denied."));

				User tUser = entityManager.find(User.class, userId);
				if(tUser == null) {
					throw new ResourceNotFoundException("User not found.");
				}
				if(!Objects.equals(tUser.getId(), user.getId())) {
					throw new AuthenticationException("Access denied.");
				}
			}

			if (requestParams.size() > 1) {
				Integer accountId = accountIdOpt.map(Integer::valueOf)
						.orElseThrow(() -> new AuthenticationException("Access denied."));

				Account account = entityManager.find(Account.class, accountId);
				if(account == null) {
					throw new ResourceNotFoundException("Account not found.");
				}
				if(!Objects.equals(account.getUser().getId(), user.getId())) {
					throw new AuthenticationException("Access denied.");
				}
			}
		}
	}
}
