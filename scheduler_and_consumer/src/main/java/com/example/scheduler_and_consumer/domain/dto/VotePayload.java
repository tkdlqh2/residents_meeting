package com.example.scheduler_and_consumer.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VotePayload(
		@JsonProperty("select_option_id")
		Long selectOptionId,
		@JsonProperty("user_id")
		Long userId
) {
}
