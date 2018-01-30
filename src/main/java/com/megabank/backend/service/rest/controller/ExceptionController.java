package com.megabank.backend.service.rest.controller;

import com.megabank.backend.service.rest.dto.RestError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;


@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<RestError> handleException(RuntimeException exception, WebRequest request) {
		ResponseStatus annotation = exception.getClass().getAnnotation(ResponseStatus.class);
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		if (annotation != null) {
			status = annotation.value();
		}
		RestError error = new RestError();
		error.setError(exception.getClass().getCanonicalName());
		error.setMessage(exception.getMessage());

		return ResponseEntity.status(status).body(error);
	}
}
