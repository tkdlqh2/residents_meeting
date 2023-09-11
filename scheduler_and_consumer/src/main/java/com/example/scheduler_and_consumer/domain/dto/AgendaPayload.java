package com.example.scheduler_and_consumer.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record AgendaPayload(
		Long Id,
		String apartmentCode,
		String title,
		String details,
		LocalDate endDate,
		Boolean secret,
		LocalDateTime createdAt,
		LocalDateTime updatedAt,
		List<SelectOptionPayload> selectOptionPayloadList

) {
	public record SelectOptionPayload(
			Long id,
			String summary,
			String details
	) {
	}
}


