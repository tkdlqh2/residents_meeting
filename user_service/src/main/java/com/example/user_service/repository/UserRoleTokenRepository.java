package com.example.user_service.repository;

import com.example.user_service.domain.Address;
import com.example.user_service.domain.UserRole;
import com.example.user_service.domain.UserRoleToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleTokenRepository extends JpaRepository<UserRoleToken, Long> {
	boolean existsByToken(String token);

	boolean existsByAddress_ApartmentCodeAndRole(String apartmentCode, UserRole role);

	boolean existsByAddressAndRole(Address address, UserRole rOle);

	Optional<UserRoleToken> findByToken(String token);
}
