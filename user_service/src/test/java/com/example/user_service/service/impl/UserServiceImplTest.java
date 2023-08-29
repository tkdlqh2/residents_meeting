package com.example.user_service.service.impl;

import com.example.user_service.domain.Address;
import com.example.user_service.domain.User;
import com.example.user_service.domain.UserRole;
import com.example.user_service.domain.dto.LogInResultDto;
import com.example.user_service.domain.dto.UserDto;
import com.example.user_service.domain.dto.UserLoginRequest;
import com.example.user_service.domain.dto.UserSignUpRequest;
import com.example.user_service.exception.UserException;
import com.example.user_service.exception.UserExceptionCode;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private JwtTokenProvider jwtTokenProvider;
	@InjectMocks
	private UserServiceImpl userServiceImplUnderTest;

	@Test
	@DisplayName("회원가입 성공")
	void singUpSuccess() {
		//given
		given(userRepository.existsByEmail(anyString())).willReturn(false);
		given(passwordEncoder.encode(anyString())).willReturn("Encoded password");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
		ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
		//when
		UserDto result = userServiceImplUnderTest.singUp(
				new UserSignUpRequest(
						"abc@gmail.com",
						"12345678",
						"홍길동",
						"01012345678",
						"A12345678",
						101,
						101
				));

		//then
		assertEquals("abc@gmail.com", result.email());
		assertEquals("홍길동", result.name());

		verify(userRepository, times(1)).save(userArgumentCaptor.capture());
		User savedUser = userArgumentCaptor.getValue();
		assertEquals("abc@gmail.com", savedUser.getEmail());
		assertEquals("Encoded password", savedUser.getPassword());
		assertEquals("홍길동", savedUser.getName());
		assertEquals("01012345678", savedUser.getPhone());
		assertEquals("A12345678", savedUser.getAddress().getApartmentCode());
		assertEquals(101, savedUser.getAddress().getBuilding());
		assertEquals(101, savedUser.getAddress().getUnit());
	}

	@Test
	@DisplayName("회원가입 실패 - 이미 존재하는 이메일")
	void singUpFail_AlreadyEmailExist() {
		//given
		given(userRepository.existsByEmail(anyString())).willReturn(true);

		//when & then
		try {
			userServiceImplUnderTest.singUp(
					new UserSignUpRequest(
							"abc@gmail.com",
							"12345678",
							"홍길동",
							"01012345678",
							"A12345678",
							101,
							101
					));

			throw new RuntimeException("예외가 발생해야 합니다.");
		} catch (Exception e) {
			assertTrue(e instanceof UserException);
			assertEquals(UserExceptionCode.EMAIL_ALREADY_EXIST.getMessage(), e.getMessage());
		}
	}

	@Test
	@DisplayName("로그인 성공")
	void loginSuccess() {
		//given
		given(userRepository.findByEmail(anyString()))
				.willReturn(Optional.of(new TestUser(1L,
						"abc@gmail.com",
						"12345678",
						"name",
						"phone",
						new Address("A12345678", 101, 101),
						UserRole.LEADER)));

		given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
		given(jwtTokenProvider.createToken(anyString(), anyList())).willReturn("token");

		//when
		LogInResultDto result = userServiceImplUnderTest.login(
				new UserLoginRequest("abc@gmail.com", "12345678"));

		//then
		assertEquals("token", result.token());
	}

	@Test
	@DisplayName("로그인 실패 - 이메일을 찾을 수 없음")
	void loginFail_EmailNotExist() {
		//given
		given(userRepository.findByEmail(anyString()))
				.willReturn(Optional.empty());

		//when & then
		try {
			userServiceImplUnderTest.login(
					new UserLoginRequest("abc@gmail.com", "12345678"));
			throw new RuntimeException("예외가 발생해야 합니다.");
		} catch (Exception e) {
			assertTrue(e instanceof UsernameNotFoundException);
			assertEquals("User not found", e.getMessage());
		}
	}

	@Test
	@DisplayName("로그인 실패 - 비밀번호 불일치")
	void loginFail_PasswordNotMatch() {
		//given
		given(userRepository.findByEmail(anyString()))
				.willReturn(Optional.of(new TestUser(1L,
						"abc@gmail.com",
						"12345678",
						"name",
						"phone",
						new Address("A12345678", 101, 101),
						UserRole.LEADER)));

		given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

		//when & then
		try {
			userServiceImplUnderTest.login(
					new UserLoginRequest("abc@gmail.com", "12345678"));
			throw new RuntimeException("예외가 발생해야 합니다.");
		} catch (Exception e) {
			assertTrue(e instanceof UserException);
			assertEquals(UserExceptionCode.PASSWORD_NOT_MATCH.getMessage(), e.getMessage());
		}
	}

	static class TestUser extends User {
		public TestUser(Long id, String email, String password, String name, String phone, Address address, UserRole role) {
			super(id, email, password, name, phone, address, role);
		}
	}
}