package com.example.user_service.service;

import com.example.user_service.domain.Address;
import com.example.user_service.domain.UserRole;
import com.example.user_service.domain.UserRoleToken;

public interface UserRoleService {
	UserRoleToken makeUserRoleToken(Address address, UserRole userRole);
	void changeUserRole(String token);

}
