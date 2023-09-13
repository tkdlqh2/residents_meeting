package com.example.scheduler_and_consumer.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record AgendaPayload(
		Long id,
		String apartmentCode,
		String title,
		String details,
		LocalDate endDate,
		Boolean secret,
		LocalDateTime createdAt,
		List<SelectOptionPayload> selectOptionPayloadList

) {
	public record SelectOptionPayload(
			Long id,
			String summary,
			String details,
			LocalDateTime createdAt
	) {
	}
}


