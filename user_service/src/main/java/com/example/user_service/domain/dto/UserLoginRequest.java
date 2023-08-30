package com.example.user_service.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserLoginRequest(
		@NotNull(message = "Email cannot be null")
		@Email
		String email,
		@NotNull(message = "Password cannot be null")
		@Size(min = 8, message = "Password must be equal or greater than 8 characters")
		String password
) {
}
