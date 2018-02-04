package com.megabank.backend.service.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megabank.backend.service.account.domain.Account;
import com.megabank.backend.service.user.domain.User;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class BaseTest {
	protected final String H_TOKEN = "X-Auth-Token";

	protected abstract MockMvc getMvc();

	protected ObjectMapper om = new ObjectMapper();

	protected User createUser(String email, String password) throws Exception {
		String json = getMvc().perform(
				post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password))
		).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		return om.readValue(json, User.class);
	}

	protected Account[] getAccounts(User user, String token) throws Exception {
		String json = getMvc().perform(
				get("/api/users/{1}/accounts", user.getId()).header(H_TOKEN, token)
		)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		return om.readValue(json, Account[].class);
	}

	protected String getToken(String email, String password) throws Exception {
		return getMvc().perform(
				post("/api/token")
						.contentType(MediaType.APPLICATION_JSON)
						.content(format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password))
		).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
	}

	protected void deleteUser(User user, String token) throws Exception {
		getMvc().perform(delete("/api/users/{1}", user.getId()).header(H_TOKEN, token))
				.andExpect(status().isOk());
	}

}
