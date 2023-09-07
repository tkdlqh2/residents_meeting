package com.example.vote_service.messagequeue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.io.Serializable;

@Getter
public abstract class Event implements Serializable {
	@JsonIgnore
	private final String topicName;
	private final String requestedMessage;

	protected Event(String topicName, Object payload) {
		this.topicName = topicName;
		this.requestedMessage = payload.toString();
	}
}
