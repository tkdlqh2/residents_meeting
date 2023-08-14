package com.example.residents_meeting.vote.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record SelectOptionCreationDto(
		@NotBlank
		String summary,
		String details
) {
}
