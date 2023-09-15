package com.example.user_service.service.impl;

import com.example.user_service.config.RequestContextHolder;
import com.example.user_service.domain.Address;
import com.example.user_service.domain.UserRole;
import com.example.user_service.domain.UserRoleToken;
import com.example.user_service.exception.UserException;
import com.example.user_service.exception.UserExceptionCode;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.repository.UserRoleTokenRepository;
import com.example.user_service.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserRoleServiceImpl implements UserRoleService {

	private final RequestContextHolder requestContextHolder;
	private final UserRepository userRepository;
	private final UserRoleTokenRepository userRoleTokenRepository;
	private static final int TOKEN_LENGTH = 30;
	private static final long VICE_LEADER_NUMBER = 5;

	@Override
	@Transactional
	public UserRoleToken makeUserRoleToken(Address address, UserRole userRole) {

		String token = UUID.randomUUID().toString().substring(0, TOKEN_LENGTH);
		while(userRoleTokenRepository.existsByToken(token)) {
			token = UUID.randomUUID().toString().substring(0, TOKEN_LENGTH);
		}

		UserRoleToken userRoleToken;
		switch (userRole) {
			case LEADER, VICE_LEADER -> {
				if (userRoleTokenRepository.existsByAddress_ApartmentCodeAndRole(address.getApartmentCode(), userRole)) {
					throw new UserException(UserExceptionCode.TOKEN_ALREADY_EXIST);
				}
			}
			case HOUSE_LEADER -> {
				if (userRoleTokenRepository.existsByAddressAndRole(address, userRole)) {
					throw new UserException(UserExceptionCode.TOKEN_ALREADY_EXIST);
				}
			}
			default -> throw new IllegalArgumentException("Invalid userRole: " + userRole);
		}

		userRoleToken = UserRoleToken.getInstance(token, userRole, address);
		userRoleToken = userRoleTokenRepository.save(userRoleToken);
		return userRoleToken;
	}

	@Override
	@Transactional
	public void changeUserRole(String token) {
		UserRoleToken userRoleToken = userRoleTokenRepository.findByToken(token)
				.orElseThrow(() -> new UserException(UserExceptionCode.TOKEN_NOT_FOUND));

		if( userRoleToken.isExpired()) {
			throw new UserException(UserExceptionCode.EXPIRED_TOKEN);
		}

		Address userAddress = requestContextHolder.getUserInfo().address();
		if(!userAddress.getApartmentCode().equals(userRoleToken.getAddress().getApartmentCode())) {
			throw new UserException(UserExceptionCode.ADDRESS_NOT_MATCH);
		}

		switch (userRoleToken.getRole()) {
			case LEADER -> {
				if (userRepository.existsByAddress_ApartmentCodeAndRole(userRoleToken.getAddress().getApartmentCode(), userRoleToken.getRole())) {
					throw new UserException(UserExceptionCode.LEADER_ALREADY_EXIST);
				}
				userRoleToken.expire();
				userRepository.findById(requestContextHolder.getUserInfo().id())
						.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND))
						.changeRole(UserRole.LEADER);
			}
			case VICE_LEADER -> {
				long viceLeaderCount = userRepository.countByAddress_ApartmentCodeAndRole(userRoleToken.getAddress().getApartmentCode(), userRoleToken.getRole());
				if (viceLeaderCount >= VICE_LEADER_NUMBER) {
					throw new UserException(UserExceptionCode.TOO_MANY_VICE_LEADER);
				} else if (viceLeaderCount == VICE_LEADER_NUMBER - 1) {
					userRoleToken.expire();
				}
				userRepository.findById(requestContextHolder.getUserInfo().id())
						.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND))
						.changeRole(UserRole.VICE_LEADER);
			}
			case HOUSE_LEADER -> {
				if (userRepository.existsByAddressAndRole(userRoleToken.getAddress(), userRoleToken.getRole())) {
					userRepository.findById(requestContextHolder.getUserInfo().id())
							.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND))
							.changeRole(UserRole.MEMBER);
				} else {
					userRepository.findById(requestContextHolder.getUserInfo().id())
							.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND))
							.changeRole(UserRole.HOUSE_LEADER);
				}
			}
			default -> throw new IllegalArgumentException("Invalid userRole: " + userRoleToken.getRole());
		}
	}
}
