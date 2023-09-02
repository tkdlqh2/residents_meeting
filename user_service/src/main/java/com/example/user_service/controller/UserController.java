package com.example.user_service.controller;

import com.example.user_service.domain.dto.*;
import com.example.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/sign-up")
	public ResponseEntity<UserDto> singUp(@RequestBody @Validated UserSignUpRequest requestLogin) {
		return ResponseEntity.ok(userService.singUp(requestLogin));
	}

	@PostMapping("/login")
	public ResponseEntity<LogInResultDto> login(@RequestBody @Validated UserLoginRequest userLoginRequest) {
		return ResponseEntity.ok(userService.login(userLoginRequest));
	}

	@GetMapping("/")
	public ResponseEntity<UserInfo> getUserInfo() {
		return ResponseEntity.ok(userService.getUserInfo());
	}
}
