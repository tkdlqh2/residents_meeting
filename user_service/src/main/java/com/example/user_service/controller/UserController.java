package com.example.user_service.controller;

import com.example.user_service.domain.Address;
import com.example.user_service.domain.UserRole;
import com.example.user_service.domain.UserRoleToken;
import com.example.user_service.domain.dto.*;
import com.example.user_service.service.UserRoleService;
import com.example.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

	private final UserService userService;
	private final UserRoleService userRoleService;

	@PostMapping("/sign-up")
	public ResponseEntity<UserRoleToken> singUp(@RequestBody @Validated UserSignUpRequest requestLogin) {
		userService.singUp(requestLogin);
		return ResponseEntity.ok(userRoleService.makeUserRoleToken(
				new Address(requestLogin.apartmentCode(),
						requestLogin.building(),
						requestLogin.unit()),
				UserRole.HOUSE_LEADER));
	}

	@PostMapping("/login")
	public ResponseEntity<LogInResultDto> login(@RequestBody @Validated UserLoginRequest userLoginRequest) {
		return ResponseEntity.ok(userService.login(userLoginRequest));
	}

	@GetMapping("/")
	public ResponseEntity<UserInfo> getUserInfo() {
		return ResponseEntity.ok(userService.getUserInfo());
	}

	@GetMapping("/{userIds}")
	public ResponseEntity<List<String>> getUserEmail(@PathVariable List<Long> userIds) {
		return ResponseEntity.ok(userService.getUserEmails(userIds));
	}
}
