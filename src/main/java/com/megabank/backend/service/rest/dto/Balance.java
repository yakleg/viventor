package com.megabank.backend.service.rest.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class Balance implements Serializable {

	private BigDecimal amount;

	public Balance() {
	}

	public Balance(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
