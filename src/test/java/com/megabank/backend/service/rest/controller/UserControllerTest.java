package com.megabank.backend.service.rest.controller;

import com.megabank.backend.Application;
import com.megabank.backend.service.user.domain.User;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class UserControllerTest extends BaseTest {

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
	public void createUser() throws Exception {
		assertNotEquals(user.getId(), null);
		assertEquals(user.getEmail(), "email@domain.com");
		assertEquals(user.getPassword(), null);

		String json = mvc.perform(
				get("/api/users/{1}", user.getId())
						.header("X-Auth-Token", token)
		)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		user = om.readValue(json, User.class);
		assertNotEquals(user.getId(), null);
		assertEquals(user.getEmail(), "email@domain.com");
		assertEquals(user.getPassword(), null);

	}

	@Test
	public void getNotExistsTest() throws Exception {
		mvc.perform(
				delete("/api/users/{1}", user.getId()).header(H_TOKEN, token)
		).andExpect(status().isOk());

		mvc.perform(
				get("/api/users/{1}", user.getId()).header(H_TOKEN, token)
		).andExpect(status().isNotFound());

		user = null;
	}

}