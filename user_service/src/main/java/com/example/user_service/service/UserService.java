package com.example.user_service.service;

import com.example.user_service.domain.dto.*;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
	LogInResultDto login(UserLoginRequest userLoginRequest);

	UserDto singUp(UserSignUpRequest requestLogin);

	UserInfo getUserInfo();
}
