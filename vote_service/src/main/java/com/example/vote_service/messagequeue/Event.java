package com.example.vote_service.messagequeue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.Serializable;

@Getter
public abstract class Event implements Serializable {
	protected static final ObjectMapper MAPPER = new ObjectMapper();
	@JsonIgnore
	private String topicName;
	private String requestedMessage;

	protected Event(String topicName, Object payload) throws JsonProcessingException {
		this.topicName = topicName;
		this.requestedMessage = MAPPER.writeValueAsString(payload);
	}
}
