package com.example.vote_service.domain.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record AgendaCreationDTO(
		@NotBlank
		@Pattern(regexp = "^A[0-9]{8}$", message = "아파트 코드는 A로 시작하고 8자리 숫자로 이루어져야 합니다.")
		String apartmentCode,
		@NotBlank
		String title,
		String details,
		@Future
		@NotNull
		LocalDate endDate,
		@NotNull
		Boolean secret,
		@NotEmpty
		List<SelectOptionCreationDto> selectOptionCreationDtoList
) {
}
