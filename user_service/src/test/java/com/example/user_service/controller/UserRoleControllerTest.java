package com.example.user_service.controller;

import com.example.user_service.TestWebSecurity;
import com.example.user_service.domain.Address;
import com.example.user_service.domain.UserRole;
import com.example.user_service.domain.UserRoleToken;
import com.example.user_service.domain.dto.UserRoleTokenRequest;
import com.example.user_service.exception.UserException;
import com.example.user_service.exception.UserExceptionCode;
import com.example.user_service.service.UserRoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(UserRoleController.class)
@Import(TestWebSecurity.class)
class UserRoleControllerTest {
	@MockBean
	private UserRoleService userRoleService;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@WithMockUser(roles = "ADMIN")
	void getTokenSuccess() throws Exception {
		//given
		given(userRoleService.makeUserRoleToken(any(), any()))
				.willReturn(UserRoleToken.getInstance("token",
						UserRole.LEADER,
						new Address("A12345678",0,0)));

		//when & then
		mockMvc.perform(
						post("/api/user/role/token").content(objectMapper.writeValueAsString(
								new UserRoleTokenRequest(
										"A12345678",
										UserRole.LEADER)
						)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
//				.andExpect(jsonPath("$.email").value("abc@gmail.com"))
//				.andExpect(jsonPath("$.name").value("홍길동"))
				.andDo(print());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void getTokenFail_MethodArgumentException() throws Exception {
		//given
		given(userRoleService.makeUserRoleToken(any(), any()))
				.willReturn(UserRoleToken.getInstance("token",
						UserRole.LEADER,
						new Address("A12345678",0,0)));

		//when & then
		mockMvc.perform(
						post("/api/user/role/token").content(objectMapper.writeValueAsString(
								new UserRoleTokenRequest(
										"A1234",
										UserRole.MEMBER)
						)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andDo(print());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void getTokenFail_UserException() throws Exception {
		//given
		doThrow(new UserException(UserExceptionCode.TOKEN_ALREADY_EXIST))
				.when(userRoleService).makeUserRoleToken(any(), any());

		//when & then
		mockMvc.perform(
						post("/api/user/role/token").content(objectMapper.writeValueAsString(
								new UserRoleTokenRequest(
										"A12345678",
										UserRole.MEMBER)
						)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(UserExceptionCode.TOKEN_ALREADY_EXIST.getStatusCode()))
				.andExpect(jsonPath("$").value(UserExceptionCode.TOKEN_ALREADY_EXIST.getMessage()))
				.andDo(print());
	}

	@Test
	@WithMockUser
	void changeUserRoleSuccess() throws Exception {
		//given
		doThrow(new UserException(UserExceptionCode.TOKEN_NOT_FOUND))
				.when(userRoleService).changeUserRole(any());

		//when & then
		mockMvc.perform(
						post("/api/user/role/change/token"))
				.andExpect(status().is(UserExceptionCode.TOKEN_NOT_FOUND.getStatusCode()))
				.andExpect(jsonPath("$").value(UserExceptionCode.TOKEN_NOT_FOUND.getMessage()))
				.andDo(print());
	}

	@Test
	@WithMockUser
	void changeUserRoleFail_UserException() throws Exception {
		//given
		doThrow(new UserException(UserExceptionCode.TOKEN_NOT_FOUND))
				.when(userRoleService).changeUserRole(any());

		//when & then
		mockMvc.perform(
						post("/api/user/role/change/token"))
				.andExpect(status().is(UserExceptionCode.TOKEN_NOT_FOUND.getStatusCode()))
				.andExpect(jsonPath("$").value(UserExceptionCode.TOKEN_NOT_FOUND.getMessage()))
				.andDo(print());
	}
}