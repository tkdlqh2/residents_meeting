package com.example.user_service.service.impl;

import com.example.user_service.config.RequestContextHolder;
import com.example.user_service.domain.Address;
import com.example.user_service.domain.User;
import com.example.user_service.domain.UserRole;
import com.example.user_service.domain.UserRoleToken;
import com.example.user_service.domain.dto.UserInfo;
import com.example.user_service.exception.UserException;
import com.example.user_service.exception.UserExceptionCode;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.repository.UserRoleTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceImplTest {

	@Mock
	private RequestContextHolder requestContextHolder;
	@Mock
	private UserRepository userRepository;
	@Mock
	private UserRoleTokenRepository userRoleTokenRepository;

	@InjectMocks
	private UserRoleServiceImpl userRoleService;

	private static final String APARTMENT_CODE = "A12345678";

	@DisplayName("role token 생성 성공 - 리더")
	@Test
	void makeUserRoleTokenSuccess_Leader() {
		//given
		given(userRoleTokenRepository.existsByToken(anyString())).willReturn(false);
		given(userRoleTokenRepository.existsByAddress_ApartmentCodeAndRole(APARTMENT_CODE, UserRole.LEADER)).willReturn(false);
		when(userRoleTokenRepository.save(any(UserRoleToken.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));
		//when
		UserRoleToken token = userRoleService.makeUserRoleToken(new Address(APARTMENT_CODE, 101, 101), UserRole.LEADER);
		//then
		verify(userRoleTokenRepository,times(1)).save(any());
		assertEquals(UserRole.LEADER,token.getRole());
		assertEquals(APARTMENT_CODE,token.getAddress().getApartmentCode());
	}

	@DisplayName("role token 생성 성공 - 집 대표")
	@Test
	void makeUserRoleTokenSuccess_HOUSE_LEADER() {
		//given
		given(userRoleTokenRepository.existsByToken(anyString())).willReturn(false);
		given(userRoleTokenRepository.existsByAddressAndRole(any(), eq(UserRole.HOUSE_LEADER))).willReturn(false);
		when(userRoleTokenRepository.save(any(UserRoleToken.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));
		//when
		UserRoleToken token = userRoleService.makeUserRoleToken(new Address(APARTMENT_CODE, 101, 101), UserRole.HOUSE_LEADER);
		//then
		verify(userRoleTokenRepository,times(1)).save(any());
		assertEquals(UserRole.HOUSE_LEADER,token.getRole());
		assertEquals(APARTMENT_CODE,token.getAddress().getApartmentCode());
	}


	@DisplayName("role token 생성 실패 - 존재하는 토큰")
	@Test
	void makeUserRoleTokenFail_TokenAlreadyExist() {
		//given
		given(userRoleTokenRepository.existsByToken(anyString())).willReturn(false);
		given(userRoleTokenRepository.existsByAddress_ApartmentCodeAndRole(any(), eq(UserRole.LEADER))).willReturn(true);
		//when & then
		try{
			userRoleService.makeUserRoleToken(new Address(APARTMENT_CODE, 101, 101), UserRole.LEADER);
			throw new RuntimeException("fail");
		} catch (Exception e) {
			assertTrue(e instanceof UserException);
			assertEquals(UserExceptionCode.TOKEN_ALREADY_EXIST.getMessage(), e.getMessage());
		}
	}

	@DisplayName("role 변경 성공 - 리더")
	@Test
	void changeUserRoleSuccess_Leader() {
		//given
		UserRoleToken token = UserRoleToken.getInstance("token", UserRole.LEADER, new Address(APARTMENT_CODE, 101, 101));
		User user = new TestUser(1L,
				"email",
				"password",
				"name",
				"phone",
				new Address(APARTMENT_CODE, 101, 101),
				UserRole.UNREGISTERED);
		given(userRoleTokenRepository.findByToken(anyString()))
				.willReturn(Optional.of(token));

		given(requestContextHolder.getUserInfo())
				.willReturn(new UserInfo(1L,
						"email",
						"name",
						"phone",
						new Address(APARTMENT_CODE, 101, 101),
						UserRole.UNREGISTERED));

		given(userRepository.existsByAddress_ApartmentCodeAndRole(anyString(), eq(UserRole.LEADER))).willReturn(false);
		given(userRepository.findById(1L))
				.willReturn(Optional.of(user));


		//when
		userRoleService.changeUserRole("token");

		//then
		verify(userRepository, times(1)).findById(anyLong());
		assertTrue(token.isExpired());
		assertEquals(UserRole.LEADER, user.getRole());
	}

	@DisplayName("role 변경 성공 - 부녀회원 & 토큰 만료됨")
	@Test
	void changeUserRoleSuccess_ViceLeaderAndTokenExpire() {
		//given
		UserRoleToken token = UserRoleToken.getInstance("token", UserRole.VICE_LEADER, new Address(APARTMENT_CODE, 101, 101));
		User user = new TestUser(1L,
				"email",
				"password",
				"name",
				"phone",
				new Address(APARTMENT_CODE, 101, 101),
				UserRole.UNREGISTERED);
		given(userRoleTokenRepository.findByToken(anyString()))
				.willReturn(Optional.of(token));

		given(requestContextHolder.getUserInfo())
				.willReturn(new UserInfo(1L,
						"email",
						"name",
						"phone",
						new Address(APARTMENT_CODE, 101, 101),
						UserRole.UNREGISTERED));

		given(userRepository.countByAddress_ApartmentCodeAndRole(APARTMENT_CODE, UserRole.VICE_LEADER))
				.willReturn(4L);
		given(userRepository.findById(1L))
				.willReturn(Optional.of(user));


		//when
		userRoleService.changeUserRole("token");

		//then
		verify(userRepository, times(1)).findById(anyLong());
		assertTrue(token.isExpired());
		assertEquals(UserRole.VICE_LEADER, user.getRole());
	}

	@DisplayName("role 변경 성공 - 부녀회원 & 토큰 만료 안됨")
	@Test
	void changeUserRoleSuccess_ViceLeaderAndTokenNotExpire() {
		//given
		UserRoleToken token = UserRoleToken.getInstance("token",
				UserRole.VICE_LEADER,
				new Address(APARTMENT_CODE, 101, 101));

		User user = new TestUser(1L,
				"email",
				"password",
				"name",
				"phone",
				new Address(APARTMENT_CODE, 101, 101),
				UserRole.UNREGISTERED);

		given(userRoleTokenRepository.findByToken(anyString()))
				.willReturn(Optional.of(token));

		given(requestContextHolder.getUserInfo())
				.willReturn(new UserInfo(1L,
						"email",
						"name",
						"phone",
						new Address(APARTMENT_CODE, 101, 101),
						UserRole.UNREGISTERED));

		given(userRepository.countByAddress_ApartmentCodeAndRole(APARTMENT_CODE, UserRole.VICE_LEADER))
				.willReturn(0L);
		given(userRepository.findById(1L))
				.willReturn(Optional.of(user));


		//when
		userRoleService.changeUserRole("token");

		//then
		verify(userRepository, times(1)).findById(anyLong());
		assertFalse(token.isExpired());
		assertEquals(UserRole.VICE_LEADER, user.getRole());
	}

	@DisplayName("role 변경 성공 - 집 대표")
	@Test
	void changeUserRoleSuccess_HouseLeader() {
		//given
		UserRoleToken token = UserRoleToken.getInstance("token", UserRole.HOUSE_LEADER, new Address(APARTMENT_CODE, 101, 101));
		User user = new TestUser(1L,
				"email",
				"password",
				"name",
				"phone",
				new Address(APARTMENT_CODE, 101, 101),
				UserRole.UNREGISTERED);
		given(userRoleTokenRepository.findByToken(anyString()))
				.willReturn(Optional.of(token));

		given(requestContextHolder.getUserInfo())
				.willReturn(new UserInfo(1L,
						"email",
						"name",
						"phone",
						new Address(APARTMENT_CODE, 101, 101),
						UserRole.UNREGISTERED));

		given(userRepository.existsByAddressAndRole(any(), eq(UserRole.HOUSE_LEADER))).willReturn(false);
		given(userRepository.findById(1L))
				.willReturn(Optional.of(user));


		//when
		userRoleService.changeUserRole("token");

		//then
		verify(userRepository, times(1)).findById(anyLong());
		assertFalse(token.isExpired());
		assertEquals(UserRole.HOUSE_LEADER, user.getRole());
	}

	@DisplayName("role 변경 성공 - 일반 회원")
	@Test
	void changeUserRoleSuccess_Member() {
		//given
		UserRoleToken token = UserRoleToken.getInstance("token", UserRole.HOUSE_LEADER, new Address(APARTMENT_CODE, 101, 101));
		User user = new TestUser(1L,
				"email",
				"password",
				"name",
				"phone",
				new Address(APARTMENT_CODE, 101, 101),
				UserRole.UNREGISTERED);

		given(userRoleTokenRepository.findByToken(anyString()))
				.willReturn(Optional.of(token));

		given(requestContextHolder.getUserInfo())
				.willReturn(new UserInfo(1L,
						"email",
						"name",
						"phone",
						new Address(APARTMENT_CODE, 101, 101),
						UserRole.UNREGISTERED));

		given(userRepository.existsByAddressAndRole(any(), eq(UserRole.HOUSE_LEADER))).willReturn(true);
		given(userRepository.findById(1L))
				.willReturn(Optional.of(user));


		//when
		userRoleService.changeUserRole("token");

		//then
		verify(userRepository, times(1)).findById(anyLong());
		assertFalse(token.isExpired());
		assertEquals(UserRole.MEMBER, user.getRole());
	}

	@DisplayName("role 변경 실패 - 토큰을 찾을 수 없음")
	@Test
	void changeUserRoleFail_NoToken() {
		//given
		given(userRoleTokenRepository.findByToken(anyString()))
				.willReturn(Optional.empty());
		//when & then
		try {
			userRoleService.changeUserRole("token");
		} catch (Exception e) {
			assertTrue(e instanceof UserException);
			assertEquals(UserExceptionCode.TOKEN_NOT_FOUND.getMessage(), e.getMessage());
		}
	}

	@DisplayName("role 변경 실패 - 만료된 토큰")
	@Test
	void changeUserRoleFail_TokenExpired() {
		//given
		UserRoleToken token = UserRoleToken.getInstance("token",
				UserRole.HOUSE_LEADER,
				new Address(APARTMENT_CODE, 101, 101));
		token.expire();
		given(userRoleTokenRepository.findByToken(anyString()))
				.willReturn(Optional.of(token));
		//when & then
		try {
			userRoleService.changeUserRole("token");
		} catch (Exception e) {
			assertTrue(e instanceof UserException);
			assertEquals(UserExceptionCode.EXPIRED_TOKEN.getMessage(), e.getMessage());
		}
	}

	@DisplayName("role 변경 실패 - 주소가 일치하지 않음")
	@Test
	void changeUserRoleFail_AddressNotMatch() {
		//given
		UserRoleToken token = UserRoleToken.getInstance("token",
				UserRole.HOUSE_LEADER,
				new Address(APARTMENT_CODE, 101, 101));

		given(userRoleTokenRepository.findByToken(anyString()))
				.willReturn(Optional.of(token));

		given(requestContextHolder.getUserInfo())
				.willReturn(new UserInfo(1L,
						"email",
						"name",
						"phone",
						new Address("A87654321", 101, 101),
						UserRole.UNREGISTERED));
		//when & then
		try {
			userRoleService.changeUserRole("token");
		} catch (Exception e) {
			assertTrue(e instanceof UserException);
			assertEquals(UserExceptionCode.ADDRESS_NOT_MATCH.getMessage(), e.getMessage());
		}
	}

	@DisplayName("role 변경 실패 - 리더가 이미 존재함")
	@Test
	void changeUserRoleFail_LeaderAlreadyExist() {
		//given
		UserRoleToken token = UserRoleToken.getInstance("token",
				UserRole.LEADER,
				new Address(APARTMENT_CODE, 101, 101));

		given(userRoleTokenRepository.findByToken(anyString()))
				.willReturn(Optional.of(token));

		given(requestContextHolder.getUserInfo())
				.willReturn(new UserInfo(1L,
						"email",
						"name",
						"phone",
						new Address(APARTMENT_CODE, 101, 101),
						UserRole.UNREGISTERED));

		given(userRepository.existsByAddress_ApartmentCodeAndRole(APARTMENT_CODE, UserRole.LEADER)).willReturn(true);

		//when & then
		try {
			userRoleService.changeUserRole("token");
		} catch (Exception e) {
			assertTrue(e instanceof UserException);
			assertEquals(UserExceptionCode.LEADER_ALREADY_EXIST.getMessage(), e.getMessage());
		}
	}

	@DisplayName("role 변경 실패 - 부녀회원이 이미 5명 존재함")
	@Test
	void changeUserRoleSuccess_TooManyViceLeaders() {
		//given
		UserRoleToken token = UserRoleToken.getInstance("token",
				UserRole.VICE_LEADER,
				new Address(APARTMENT_CODE, 101, 101));

		given(userRoleTokenRepository.findByToken(anyString()))
				.willReturn(Optional.of(token));

		given(requestContextHolder.getUserInfo())
				.willReturn(new UserInfo(1L,
						"email",
						"name",
						"phone",
						new Address(APARTMENT_CODE, 101, 101),
						UserRole.UNREGISTERED));

		given(userRepository.countByAddress_ApartmentCodeAndRole(APARTMENT_CODE, UserRole.VICE_LEADER))
				.willReturn(5L);

		//when & then
		try {
			userRoleService.changeUserRole("token");
		} catch (Exception e) {
			assertTrue(e instanceof UserException);
			assertEquals(UserExceptionCode.TOO_MANY_VICE_LEADER.getMessage(), e.getMessage());
		}
	}

	static class TestUser extends User {

		public TestUser(Long id,
						String email,
						String password,
						String name,
						String phone,
						Address address,
						UserRole role) {
			super(id, email, password, name, phone, address, role);
		}
		public void changeRole(UserRole role) {
			super.changeRole(role);
		}
	}
}