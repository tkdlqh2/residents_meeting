package com.example.user_service.domain.dto;

import com.example.user_service.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRoleTokenRequest(
		@NotBlank(message = "apartment info cannot be blank")
		@Pattern(regexp = "^A[0-9]{8}$", message = "wrong type of apartment info")
		String apartmentCode,
		UserRole userRole
){
}

