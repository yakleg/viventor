package com.megabank.backend.service.rest.controller;

import com.megabank.backend.Application;
import com.megabank.backend.service.account.domain.Account;
import com.megabank.backend.service.user.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class SecurityTest extends BaseTest {
	@Autowired
	private MockMvc mvc;

	@Override
	protected MockMvc getMvc() {
		return mvc;
	}

	@Test
	public void userCantSeeAnotherUser() throws Exception {
		User firstUser = createUser("first@domain.com", "secret");
		User secondUser = createUser("second@domain.com", "secret");

		String firstToken = getToken("first@domain.com", "secret");
		String secondToken = getToken("second@domain.com", "secret");

		mvc.perform(get("/api/users/{1}", secondUser.getId()).header(H_TOKEN, firstToken))
				.andExpect(status().isForbidden());

		mvc.perform(get("/api/users/{1}", firstUser.getId()).header(H_TOKEN, firstToken))
				.andExpect(status().isOk());

		mvc.perform(get("/api/users/{1}", firstUser.getId()).header(H_TOKEN, secondToken))
				.andExpect(status().isForbidden());

		mvc.perform(get("/api/users/{1}", secondUser.getId()).header(H_TOKEN, secondToken))
				.andExpect(status().isOk());


		deleteUser(firstUser, firstToken);
		deleteUser(secondUser, secondToken);
	}

	@Test
	public void userCantUseAnotherAccount() throws Exception {
		User firstUser = createUser("first@domain.com", "secret");
		User secondUser = createUser("second@domain.com", "secret");

		String firstToken = getToken("first@domain.com", "secret");
		String secondToken = getToken("second@domain.com", "secret");

		Account[] firstAccounts = getAccounts(firstUser, firstToken);
		Account[] secondAccounts = getAccounts(secondUser, secondToken);

		assertTrue(firstAccounts.length == 1);
		assertTrue(secondAccounts.length == 1);

		mvc.perform(get("/api/users/{1}/accounts/{2}/postings", firstUser.getId(), secondAccounts[0].getId())
				.header(H_TOKEN, firstToken))
				.andExpect(status().isForbidden());

		mvc.perform(get("/api/users/{1}/accounts/{2}/balance", firstUser.getId(), secondAccounts[0].getId())
				.header(H_TOKEN, firstToken))
				.andExpect(status().isForbidden());


		deleteUser(firstUser, firstToken);
		deleteUser(secondUser, secondToken);
	}

	@Test
	public void notPossibleUseRevokedToken() throws Exception {
		User user = createUser("email@domain.com", "secret");
		String token = getToken("email@domain.com", "secret");

		mvc.perform(delete("/api/token").header(H_TOKEN, token))
				.andExpect(status().isOk());
		mvc.perform(get("/api/users/{1}", user.getId()).header(H_TOKEN, token))
				.andExpect(status().isForbidden());

		token = getToken("email@domain.com", "secret");
		deleteUser(user, token);
	}
}
