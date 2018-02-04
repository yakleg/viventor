package com.megabank.backend.service.rest.controller;

import com.megabank.backend.Application;
import com.megabank.backend.service.account.domain.Account;
import com.megabank.backend.service.account.domain.Posting;
import com.megabank.backend.service.user.domain.User;
import com.megabank.backend.service.rest.dto.Balance;
import com.megabank.backend.service.rest.dto.RestError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class AccountControllerTest extends BaseTest {

	private User user;
	private String token;

	@Autowired
	private MockMvc mvc;

	@Override
	protected MockMvc getMvc() {
		return mvc;
	}

	@Before
	public void setup() throws Exception {
		user = createUser("email@domain.com", "secretPassword");
		token = getToken("email@domain.com", "secretPassword");
	}

	@After
	public void tearDown() throws Exception {
		if (user == null) return;
		deleteUser(user, token);
	}

	@Test
	public void defaultAccountCreatedTest() throws Exception {
		Account[] accounts = getAccounts(user, token);
		assertEquals(accounts.length, 1);

		Account account = accounts[0];
		assertEquals(account.getName(), "Default");
		assertEquals(account.getCurrency(), Currency.getInstance("USD"));

		String json = mvc.perform(
				get("/api/users/{1}/accounts/{2}/postings", user.getId(), account.getId()).header(H_TOKEN, token)
		)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		assertEquals(json, "[]");

		json = mvc.perform(
				get("/api/users/{1}/accounts/{2}/balance", user.getId(), account.getId()).header(H_TOKEN, token)
		)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		Balance balance = om.readValue(json, Balance.class);

		assertEquals(balance.getAmount().compareTo(BigDecimal.ZERO), 0);
	}

	@Test
	public void deleteLastAccount() throws Exception {
		Account[] accounts = getAccounts(user, token);
		assertEquals(accounts.length, 1);

		Account account = accounts[0];
		assertEquals(account.getName(), "Default");
		assertEquals(account.getCurrency(), Currency.getInstance("USD"));

		String json = mvc.perform(
				delete("/api/users/{1}/accounts/{2}", user.getId(), account.getId()).header(H_TOKEN, token)
		)
				.andExpect(status().is4xxClientError())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		RestError error = om.readValue(json, RestError.class);

		assertEquals(error.getMessage(), "Can't delete last account.");
	}

	@Test
	public void createDeleteAdditionalAccount() throws Exception {
		String json = mvc.perform(
				post("/api/users/{1}/accounts", user.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\": \"Additional account\", \"currency\": \"EUR\"}")
						.header(H_TOKEN, token)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Account account = om.readValue(json, Account.class);

		assertEquals(account.getName(), "Additional account");
		assertEquals(account.getCurrency(), Currency.getInstance("EUR"));

		mvc.perform(
				delete("/api/users/{1}/accounts/{2}", user.getId(), account.getId()).header(H_TOKEN, token)
		).andExpect(status().isOk());

		Account[] accounts = getAccounts(user, token);
		assertEquals(accounts.length, 1);

		account = accounts[0];
		assertEquals(account.getName(), "Default");
		assertEquals(account.getCurrency(), Currency.getInstance("USD"));
	}

	@Test
	public void depositAndWithdrawAbilityTest() throws Exception {
		Account[] accounts = getAccounts(user, token);
		assertTrue(accounts.length == 1);
		Account account = accounts[0];

		//put on deposit 1000
		mvc.perform(
				post("/api/users/{1}/accounts/{2}/postings", user.getId(), account.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"amount\": 1000, \"description\": \"My deposit\"}")
						.header(H_TOKEN, token)
		).andExpect(status().isOk());

		//check balance
		String json = mvc.perform(
				get("/api/users/{1}/accounts/{2}/balance", user.getId(), account.getId()).header(H_TOKEN, token)
		)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		Balance balance = om.readValue(json, Balance.class);

		assertEquals(balance.getAmount().compareTo(new BigDecimal(1000)), 0);

		mvc.perform(
				post("/api/users/{1}/accounts/{2}/postings", user.getId(), account.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"amount\": -226, \"description\": \"Spent some money\"}")
						.header(H_TOKEN, token)
		)
				.andExpect(status().isOk());

		json = mvc.perform(
				get("/api/users/{1}/accounts/{2}/balance", user.getId(), account.getId()).header(H_TOKEN, token)
		)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		balance = om.readValue(json, Balance.class);

		assertEquals(balance.getAmount().compareTo(new BigDecimal(774)), 0);

		json = mvc.perform(
				delete("/api/users/{1}", user.getId()).header(H_TOKEN, token)
		)
				.andExpect(status().is4xxClientError())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		RestError error = om.readValue(json, RestError.class);

		assertEquals(error.getMessage(), "Can't delete user with positive balance.");

		json = mvc.perform(
				post("/api/users/{1}/accounts/{2}/postings", user.getId(), account.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"amount\": -1000, \"description\": \"Spent some money\"}")
						.header(H_TOKEN, token)
		)
				.andExpect(status().is4xxClientError())
				.andReturn().getResponse().getContentAsString();
		error = om.readValue(json, RestError.class);
		assertEquals(error.getMessage(), "Insufficient funds on account.");

		mvc.perform(
				post("/api/users/{1}/accounts/{2}/postings", user.getId(), account.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"amount\": -774, \"description\": \"Spent some money\"}")
						.header(H_TOKEN, token)
		).andExpect(status().isOk());

	}

	public void cantCreateTwoAccountWithSameName() throws Exception {
		mvc.perform(
				post("/api/users/{1}/accounts", user.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\": \"Additional account\", \"currency\": \"EUR\"}")
						.header(H_TOKEN, token)
		)
				.andExpect(status().isOk());

		mvc.perform(
				post("/api/users/{1}/accounts", user.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\": \"Additional account\", \"currency\": \"EUR\"}")
						.header(H_TOKEN, token)
		)
				.andExpect(status().is4xxClientError());

	}

	@Test
	public void statementTest() throws Exception {
		Account[] accounts = getAccounts(user, token);
		assertTrue(accounts.length == 1);
		Account account = accounts[0];

		mvc.perform(
				post("/api/users/{1}/accounts/{2}/postings", user.getId(), account.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"amount\": 1000, \"description\": \"First deposit\"}")
						.header(H_TOKEN, token)
		).andExpect(status().isOk());

		mvc.perform(
				post("/api/users/{1}/accounts/{2}/postings", user.getId(), account.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"amount\": 1000, \"description\": \"Second deposit\"}")
						.header(H_TOKEN, token)
		).andExpect(status().isOk());

		mvc.perform(
				post("/api/users/{1}/accounts/{2}/postings", user.getId(), account.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"amount\": -1000, \"description\": \"First spent\"}")
						.header(H_TOKEN, token)
		).andExpect(status().isOk());

		mvc.perform(
				post("/api/users/{1}/accounts/{2}/postings", user.getId(), account.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"amount\": -1000, \"description\": \"Second spent\"}")
						.header(H_TOKEN, token)
		).andExpect(status().isOk());

		String json = mvc.perform(
				get("/api/users/{1}/accounts/{2}/postings", user.getId(), account.getId()).header(H_TOKEN, token)
		)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		Posting[] postings = om.readValue(json, Posting[].class);
		assertEquals(postings.length, 4);

		assertEquals(postings[0].getDescription(), "First deposit");
		assertNotNull(postings[0].getCreated());
		assertEquals(postings[0].getAmount().longValue(), 1000L);

		assertEquals(postings[1].getDescription(), "Second deposit");
		assertNotNull(postings[1].getCreated());
		assertEquals(postings[1].getAmount().longValue(), 1000L);

		assertEquals(postings[2].getDescription(), "First spent");
		assertNotNull(postings[2].getCreated());
		assertEquals(postings[2].getAmount().longValue(), -1000L);

		assertEquals(postings[3].getDescription(), "Second spent");
		assertNotNull(postings[3].getCreated());
		assertEquals(postings[3].getAmount().longValue(), -1000L);
	}
}