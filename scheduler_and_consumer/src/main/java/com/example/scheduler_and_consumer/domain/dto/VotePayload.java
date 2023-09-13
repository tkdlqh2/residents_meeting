package com.example.scheduler_and_consumer.domain.dto;

import java.time.LocalDateTime;

public record VotePayload(
		Long selectOptionId,
		Long userId,
		LocalDateTime createdAt
) {
}
