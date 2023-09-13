package com.example.user_service.controller;

import com.example.user_service.TestWebSecurity;

import com.example.user_service.domain.Address;
import com.example.user_service.domain.UserRole;
import com.example.user_service.domain.dto.*;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
	@DisplayName("회원 등록 실패 - 유효하지 않은 입력")
	void registerFail_IllegalArguments() throws Exception {
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
										"01012345678999",
										"A1234567adfasdf8",
										101,
										101
								)
						)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andDo(print());
	}

	@Test
	@DisplayName("회원 등록 실패 - 이미 존재하는 이메일")
	void registerFail_AlreadyEmailExist() throws Exception {
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
	@DisplayName("회원 등록 실패 - 이미 존재하는 핸드폰 번호")
	void registerFail_AlreadyPhoneExist() throws Exception {
		//given
		doThrow(new UserException(UserExceptionCode.PHONE_ALREADY_EXIST))
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
				.andExpect(status().is(UserExceptionCode.PHONE_ALREADY_EXIST.getStatusCode()))
				.andExpect(jsonPath("$").value(UserExceptionCode.PHONE_ALREADY_EXIST.getMessage()))
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

	@Test
	@DisplayName("유저 정보 가져오기 성공")
	@WithMockUser(username = "abc")
	void getUserInfoSuccess() throws Exception {
		//given
		doThrow(new UserException(UserExceptionCode.PASSWORD_NOT_MATCH))
				.when(userService).login(any(UserLoginRequest.class));

		given(userService.getUserInfo())
				.willReturn(new UserInfo(1L,
						"abc@gmail.com",
						"홍길동",
						"01012345678",
						new Address("A12345678",101,101),
						UserRole.MEMBER));
		//when & then
		mockMvc.perform(get("/api/user/").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.email").value("abc@gmail.com"))
				.andExpect(jsonPath("$.name").value("홍길동"))
				.andExpect(jsonPath("$.phone").value("01012345678"))
				.andExpect(jsonPath("$.address.apartmentCode").value("A12345678"))
				.andExpect(jsonPath("$.address.building").value(101))
				.andExpect(jsonPath("$.address.unit").value(101))
				.andExpect(jsonPath("$.userRole").value(UserRole.MEMBER.name()))
				.andDo(print());
	}


	@Test
	@DisplayName("유저 이메일 가져오기 성공")
	@WithMockUser()
	void getUserEmailSuccess() throws Exception {
		//given
		given(userService.getUserEmails(any(List.class)))
				.willReturn(List.of("a","b"));

		//when & then
		mockMvc.perform(
						get("/api/user/1,2").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0]").value("a"))
				.andExpect(jsonPath("$[1]").value("b"))
				.andExpect(jsonPath("$[2]").doesNotExist())
				.andDo(print());
	}
}