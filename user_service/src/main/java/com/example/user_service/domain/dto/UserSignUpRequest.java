package com.example.user_service.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserSignUpRequest(
		@Email
		@NotBlank(message = "Email cannot be blank")
		String email,
		@Size(min = 8, message = "Password must be equal or greater than 8 characters")
		@NotBlank(message = "Password cannot be blank")
		String password,
		@NotBlank(message = "Name cannot be blank")
		String name,
		@NotBlank(message = "Phone cannot be blank")
		@Pattern(regexp = "^[0-9]{11}$", message = "Phone number must be 10 digits")
		String phone,
		@NotBlank(message = "apartment info cannot be blank")
		@Pattern(regexp = "^A[0-9]{8}$", message = "wrong type of apartment info")
		String apartmentCode,
		int building,
		int unit
) {
}


