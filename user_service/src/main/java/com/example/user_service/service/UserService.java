package com.example.user_service.service;

import com.example.user_service.domain.dto.LogInResultDto;
import com.example.user_service.domain.dto.UserDto;
import com.example.user_service.domain.dto.UserLoginRequest;
import com.example.user_service.domain.dto.UserSignUpRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
	LogInResultDto login(UserLoginRequest userLoginRequest);

	UserDto singUp(UserSignUpRequest requestLogin);
}