package com.megabank.backend.service.rest.controller;

import com.megabank.backend.Application;
import com.megabank.backend.service.domain.Account;
import com.megabank.backend.service.domain.Posting;
import com.megabank.backend.service.domain.User;
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

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class PostingControllerTest extends BaseTest {
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
	public void statementTest() throws Exception {

		String accountJson = mvc.perform(
				get("/api/users/{1}/accounts", user.getId()).header(H_TOKEN, token)
		)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		Account account = om.readValue(accountJson, Account[].class)[0];

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
		assertEquals(postings.length,4);

		assertNotNull(postings[0].getId());
		assertEquals(postings[0].getDescription(), "First deposit");
		assertNotNull(postings[0].getCreated());
		assertEquals(postings[0].getAmount().longValue(), 1000L);

		assertNotNull(postings[1].getId());
		assertEquals(postings[1].getDescription(), "Second deposit");
		assertNotNull(postings[1].getCreated());
		assertEquals(postings[1].getAmount().longValue(), 1000L);

		assertNotNull(postings[2].getId());
		assertEquals(postings[2].getDescription(), "First spent");
		assertNotNull(postings[2].getCreated());
		assertEquals(postings[2].getAmount().longValue(), -1000L);

		assertNotNull(postings[3].getId());
		assertEquals(postings[3].getDescription(), "Second spent");
		assertNotNull(postings[3].getCreated());
		assertEquals(postings[3].getAmount().longValue(), -1000L);
	}
}