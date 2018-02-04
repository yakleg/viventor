package com.megabank.backend.service.user.api;

import com.megabank.backend.service.user.domain.User;

import java.util.Optional;

public interface UserManager {

	Optional<User> find(int userId);

	Optional<User> findByEmail(String email);

	User create(User user);

	void delete(int userId);
}
