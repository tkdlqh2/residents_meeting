package com.example.vote_service.domain.dto;

import com.example.vote_service.messagequeue.Field;
import com.example.vote_service.messagequeue.KafkaDto;
import com.example.vote_service.messagequeue.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class VoteEvent extends KafkaDto {
	private final Schema schema = Schema.builder()
			.type("struct")
			.fields(List.of(
					new Field("int32", false, "select_option_id"),
					new Field("int32", false, "user_id")))
			.optional(false)
			.name("vote")
			.build();
	private final VotePayload payload;

	private VoteEvent(VotePayload votePayload) {
		super("vote_sink");
		this.payload = votePayload;
	}

	public VoteEvent(Long selectOptionId, Long userId) {
		this(new VotePayload(selectOptionId, userId));
	}

	public record VotePayload(
			@JsonProperty("select_option_id")
			Long selectOptionId,
			@JsonProperty("user_id")
			Long userId
	) {
	}
}
