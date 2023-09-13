package com.example.user_service.service.impl;

import com.example.user_service.config.RequestContextHolder;
import com.example.user_service.domain.User;
import com.example.user_service.domain.dto.*;
import com.example.user_service.exception.UserException;
import com.example.user_service.exception.UserExceptionCode;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.security.JwtTokenProvider;
import com.example.user_service.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RequestContextHolder requestContextHolder;

	public UserServiceImpl(UserRepository userRepository,
						   PasswordEncoder passwordEncoder,
						   JwtTokenProvider jwtTokenProvider,
						   RequestContextHolder requestContextHolder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
		this.requestContextHolder = requestContextHolder;
	}

	@Override
	public UserDto singUp(UserSignUpRequest requestLogin) {
		if (userRepository.existsByEmail(requestLogin.email())) {
			throw new UserException(UserExceptionCode.EMAIL_ALREADY_EXIST);
		}

		if (userRepository.existsByPhone(requestLogin.phone())) {
			throw new UserException(UserExceptionCode.PHONE_ALREADY_EXIST);
		}

		User user = User.fromSignUpRequestAndPasswordEncoder(requestLogin, passwordEncoder);
		return userRepository.save(user).toUserDto();
	}

	@Override
	public LogInResultDto login(UserLoginRequest userLoginRequest) {
		User user = userRepository.findByEmail(userLoginRequest.email())
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		if (!passwordEncoder.matches(userLoginRequest.password(), user.getPassword())) {
			throw new UserException(UserExceptionCode.PASSWORD_NOT_MATCH);
		}

		return new LogInResultDto(jwtTokenProvider.createToken(user.getEmail(), List.of(user.getRole().name())));
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		return userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	@Override
	public UserInfo getUserInfo() {
		return requestContextHolder.getUserInfo();
	}

	@Override
	public List<String> getUserEmails(List<Long> userIds) {
		return userRepository.findAllById(userIds).stream()
				.map(User::getEmail)
				.toList();
	}
}
