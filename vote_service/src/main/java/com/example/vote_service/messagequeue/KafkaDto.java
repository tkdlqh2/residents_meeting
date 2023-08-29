package com.example.vote_service.messagequeue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.io.Serializable;

@Getter
public abstract class KafkaDto implements Serializable {
	@JsonIgnore
	public String topicName;

	protected KafkaDto(String topicName) {
		this.topicName = topicName;
	}
}
