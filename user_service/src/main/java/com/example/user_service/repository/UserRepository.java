package com.example.user_service.repository;


import com.example.user_service.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String username);

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);
}
