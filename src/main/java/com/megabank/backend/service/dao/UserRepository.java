package com.megabank.backend.service.dao;

import com.megabank.backend.service.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmailAndPassword(String email, String password);
}
