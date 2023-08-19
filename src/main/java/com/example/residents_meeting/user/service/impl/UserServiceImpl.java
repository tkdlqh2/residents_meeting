package com.example.residents_meeting.user.service.impl;

import com.example.residents_meeting.user.domain.User;
import com.example.residents_meeting.user.domain.dto.LogInResultDto;
import com.example.residents_meeting.user.domain.dto.UserDto;
import com.example.residents_meeting.user.domain.dto.UserLoginRequest;
import com.example.residents_meeting.user.domain.dto.UserSignUpRequest;
import com.example.residents_meeting.user.exception.UserException;
import com.example.residents_meeting.user.exception.UserExceptionCode;
import com.example.residents_meeting.user.repository.UserRepository;
import com.example.residents_meeting.user.security.JwtTokenProvider;
import com.example.residents_meeting.user.service.UserService;
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

	public UserServiceImpl(UserRepository userRepository,
						   PasswordEncoder passwordEncoder,
						   JwtTokenProvider jwtTokenProvider) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public UserDto singUp(UserSignUpRequest requestLogin) {
		if (userRepository.existsByEmail(requestLogin.email())) {
			throw new UserException(UserExceptionCode.EMAIL_ALREADY_EXIST);
		}

		User user = User.fromSignUpRequestAndPasswordEncoder(requestLogin, passwordEncoder);
		return userRepository.save(user).toUserDto();
	}

	@Override
	public LogInResultDto login(UserLoginRequest userLoginRequest) {
		User user = userRepository.findByEmail(userLoginRequest.email())
				.orElseThrow( () -> new UsernameNotFoundException("User not found"));

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
}
