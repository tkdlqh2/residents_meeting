package com.example.residents_meeting.user.service;

import com.example.residents_meeting.user.domain.dto.LogInResultDto;
import com.example.residents_meeting.user.domain.dto.UserDto;
import com.example.residents_meeting.user.domain.dto.UserLoginRequest;
import com.example.residents_meeting.user.domain.dto.UserSignUpRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
	LogInResultDto login(UserLoginRequest userLoginRequest);

	UserDto singUp(UserSignUpRequest requestLogin);
}
