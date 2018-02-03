package com.megabank.backend.service.rest.controller;

import com.megabank.backend.service.rest.dto.RestError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.lang.invoke.MethodHandles;
import java.util.Optional;


@ControllerAdvice
public class ExceptionController {

	private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<RestError> handleException(RuntimeException exception, WebRequest request) {
		HttpStatus status = Optional.ofNullable(exception.getClass().getAnnotation(ResponseStatus.class))
				.map(ResponseStatus::value)
				.orElse(HttpStatus.INTERNAL_SERVER_ERROR);

		RestError error = new RestError();
		error.setError(exception.getClass().getCanonicalName());
		error.setMessage(exception.getMessage());

		log.error("Response ");
		return ResponseEntity.status(status).body(error);
	}
}
