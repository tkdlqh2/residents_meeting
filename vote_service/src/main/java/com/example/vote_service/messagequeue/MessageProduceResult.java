package com.example.vote_service.messagequeue;

import lombok.Getter;

@Getter
public class MessageProduceResult {
	private Boolean status = true;
	private String topic;
	private String requestedMessage;
	private String errorMessage = null;
	public MessageProduceResult(Event event) {
		this.topic = event.getTopicName();
		this.requestedMessage = event.getRequestedMessage();
	}

	public MessageProduceResult(Event event, Throwable e) {
		this(event);
		this.status = false;
		this.errorMessage = e.getMessage();
	}
}
