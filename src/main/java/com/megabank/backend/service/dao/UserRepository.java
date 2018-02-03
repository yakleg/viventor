package com.megabank.backend.service.dao;

import com.megabank.backend.service.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByEmailAndPassword(String email, String password);
}
