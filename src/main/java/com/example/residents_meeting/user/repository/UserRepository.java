package com.example.residents_meeting.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.residents_meeting.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String username);
	boolean existsByEmail(String email);
}
