package com.example.vote_service.domain.dto;

import com.example.vote_service.domain.SelectOptionHistory;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SelectOptionVo(
		Long id,
		Long agendaId,
		String summary,
		String details,
		LocalDateTime createdAt,
		LocalDateTime updatedAt){


	public SelectOptionHistory toSelectOptionHistory() {

		return SelectOptionHistory.builder()
				.summary(summary)
				.details(details)
				.build();
	}
}
