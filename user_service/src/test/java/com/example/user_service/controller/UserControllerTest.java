package com.example.user_service.controller;

import com.example.user_service.TestWebSecurity;

import com.example.user_service.domain.dto.LogInResultDto;
import com.example.user_service.domain.dto.UserDto;
import com.example.user_service.domain.dto.UserLoginRequest;
import com.example.user_service.domain.dto.UserSignUpRequest;
import com.example.user_service.exception.UserException;
import com.example.user_service.exception.UserExceptionCode;
import com.example.user_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(UserController.class)
@Import(TestWebSecurity.class)
class UserControllerTest {

	@MockBean
	private UserService userService;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("회원 등록 성공")
	void registerSuccess() throws Exception {
		//given
		given(userService.singUp(any(UserSignUpRequest.class)))
				.willReturn(new UserDto("abc@gmail.com", "홍길동"));

		//when & then
		mockMvc.perform(
						post("/api/user/sign-up").content(objectMapper.writeValueAsString(
								new UserSignUpRequest(
										"abc@gmail.com",
										"12345678",
										"홍길동",
										"01012345678",
										"A12345678",
										101,
										101
								)
						)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("abc@gmail.com"))
				.andExpect(jsonPath("$.name").value("홍길동"))
				.andDo(print());
	}

	@Test
	@DisplayName("회원 등록 실패 - 이미 존재하는 이메일")
	void register() throws Exception {
		//given
		doThrow(new UserException(UserExceptionCode.EMAIL_ALREADY_EXIST))
				.when(userService).singUp(any(UserSignUpRequest.class));

		//when & then
		mockMvc.perform(
						post("/api/user/sign-up").content(objectMapper.writeValueAsString(
								new UserSignUpRequest(
										"abc@gmail.com",
										"12345678",
										"홍길동",
										"01012345678",
										"A12345678",
										101,
										101
								)
						)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(UserExceptionCode.EMAIL_ALREADY_EXIST.getStatusCode()))
				.andExpect(jsonPath("$").value(UserExceptionCode.EMAIL_ALREADY_EXIST.getMessage()))
				.andDo(print());

	}

	@Test
	@DisplayName("로그인 성공")
	void loginSuccess() throws Exception {
		//given
		given(userService.login(any(UserLoginRequest.class)))
				.willReturn(new LogInResultDto("12345678"));

		//when & then
		mockMvc.perform(
						post("/api/user/login").content(objectMapper.writeValueAsString(
								new UserLoginRequest(
										"abc@gmail.com",
										"12345678")
						)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value("12345678"))
				.andDo(print());
	}

	@Test
	@DisplayName("로그인 실패 - 비밀번호 불일치")
	void loginFail_PasswordNotMatch() throws Exception {
		//given
		doThrow(new UserException(UserExceptionCode.PASSWORD_NOT_MATCH))
				.when(userService).login(any(UserLoginRequest.class));

		//when & then
		mockMvc.perform(
						post("/api/user/login").content(objectMapper.writeValueAsString(
								new UserLoginRequest(
										"abc@gmail.com",
										"12345678")
						)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(UserExceptionCode.PASSWORD_NOT_MATCH.getStatusCode()))
				.andExpect(jsonPath("$").value(UserExceptionCode.PASSWORD_NOT_MATCH.getMessage()))
				.andDo(print());
	}
}