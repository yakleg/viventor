package com.megabank.backend.service.rest.dto;

import java.io.Serializable;

public class RestError implements Serializable {
	private String error;
	private String message;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
