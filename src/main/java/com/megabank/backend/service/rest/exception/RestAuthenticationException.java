package com.megabank.backend.service.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RestAuthenticationException extends RuntimeException {

	public RestAuthenticationException(String message) {
		super(message);
	}
}
