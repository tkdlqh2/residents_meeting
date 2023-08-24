package com.example.residents_meeting.common.config.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducer {
	private final KafkaTemplate<String, String> kafkaTemplate;
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public KafkaDto send(KafkaDto kafkaDto) {
		String jsonInString = "";
		try {
			jsonInString = objectMapper.writeValueAsString(kafkaDto);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		kafkaTemplate.send(kafkaDto.getTopicName(), jsonInString);
		return kafkaDto;
	}
}
