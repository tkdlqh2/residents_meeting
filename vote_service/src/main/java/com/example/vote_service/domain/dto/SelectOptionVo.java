package com.example.vote_service.domain.dto;

import com.example.vote_service.domain.SelectOptionHistory;
import lombok.Builder;

@Builder
public record SelectOptionVo(
		Long id,
		Long agendaId,
		String summary,
		String details){
	public SelectOptionHistory toSelectOptionHistory() {

		return SelectOptionHistory.builder()
				.agendaId(agendaId)
				.summary(summary)
				.details(details)
				.build();
	}
}
