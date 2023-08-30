package com.example.vote_service.domain.dto;

import com.example.vote_service.domain.Agenda;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SelectOptionVo(
		Long id,
		Agenda agenda,
		String summary,
		String details,
		LocalDateTime createdAt,
		LocalDateTime updatedAt){
}
