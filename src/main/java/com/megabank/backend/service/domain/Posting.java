package com.megabank.backend.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "postings")
public class Posting {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false, columnDefinition = "DECIMAL(7,2)")
	private BigDecimal amount;

	@ManyToOne
	@JoinColumn(name = "account_id")
	@JsonIgnore
	private Account account;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private Date created;

	private String description;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Date getCreated() {
		return created;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
