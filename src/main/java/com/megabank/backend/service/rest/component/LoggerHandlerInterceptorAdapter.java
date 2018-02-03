package com.megabank.backend.service.rest.component;

import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoggerHandlerInterceptorAdapter extends HandlerInterceptorAdapter {
	static private final AtomicInteger COUNTER = new AtomicInteger();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String context = String.format("REST:%06d", COUNTER.addAndGet(1));

		MDC.put("context", context);
		Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
				.ifPresent(authentication -> MDC.put("actor", authentication.getPrincipal().toString()));
		return true;
	}
}
