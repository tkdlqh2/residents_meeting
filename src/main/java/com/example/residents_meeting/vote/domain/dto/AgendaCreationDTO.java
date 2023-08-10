package com.example.residents_meeting.vote.domain.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record AgendaCreationDTO(
		@NotBlank
		@Pattern(regexp = "^A[0-9]{8}$")
		String apartmentCode,
		@NotBlank
		String title,
		String details,
		@Future
		@NotNull
		LocalDate endDate,
		@NotEmpty
		List<SelectOptionCreationDto> selectOptionCreationDtoList
) {
}
