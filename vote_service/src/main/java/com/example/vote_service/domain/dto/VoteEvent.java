package com.example.vote_service.domain.dto;

import com.example.vote_service.messagequeue.Event;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;

@Getter
public class VoteEvent extends Event {

	private VoteEvent(VotePayload votePayload) throws JsonProcessingException {
		super("vote_sink", votePayload);
	}

	public static VoteEvent toEvent(VoteCreationDto voteCreationDto) {
		try {
			return new VoteEvent(new VotePayload(voteCreationDto.selectOptionId(), voteCreationDto.userId()));
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	private record VotePayload(
			@JsonProperty("select_option_id")
			Long selectOptionId,
			@JsonProperty("user_id")
			Long userId
	) {
	}
}
