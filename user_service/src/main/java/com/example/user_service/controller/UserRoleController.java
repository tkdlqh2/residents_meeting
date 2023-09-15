package com.example.user_service.controller;

import com.example.user_service.domain.Address;
import com.example.user_service.domain.UserRole;
import com.example.user_service.domain.UserRoleToken;
import com.example.user_service.domain.dto.UserRoleTokenRequest;
import com.example.user_service.service.UserRoleService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/role")
public class UserRoleController {

	private final UserRoleService userRoleService;

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/token")
	public ResponseEntity<UserRoleToken> getToken(@RequestBody @Validated UserRoleTokenRequest request) {
		if (request.userRole() == UserRole.LEADER || request.userRole() == UserRole.VICE_LEADER) {
			return ResponseEntity.ok(userRoleService.makeUserRoleToken(
					new Address(request.apartmentCode(), 0,0),
					UserRole.LEADER));
		}
		throw new IllegalArgumentException("Invalid userRole: " + request.userRole());
	}

	@PostMapping("/change/{token}")
	public ResponseEntity<String> changeUserRole(@PathVariable String token) {
		userRoleService.changeUserRole(token);
		return ResponseEntity.ok("success");
	}
}
