package com.example.user_service.service;

import com.example.user_service.domain.dto.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
	LogInResultDto login(UserLoginRequest userLoginRequest);

	UserDto singUp(UserSignUpRequest requestLogin);

	UserInfo getUserInfo();

	List<String> getUserEmails(List<Long> userIds);
}
