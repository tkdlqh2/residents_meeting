package com.example.vote_service.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class KafkaProducer {
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}

	public KafkaDto send(KafkaDto kafkaDto) {
		String jsonInString = "";
		try {
			jsonInString = objectMapper.writeValueAsString(kafkaDto);
			System.out.println(jsonInString);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(kafkaDto.getTopicName(), jsonInString);
		String finalJsonInString = jsonInString;
		future.whenComplete((result, ex) -> {
			if (ex == null) {
				log.info("Sent message=[" + finalJsonInString +
						"] with offset=[" + result.getRecordMetadata().offset() + "]");
			} else {
				log.info("Unable to send message=[" +
						finalJsonInString + "] due to : " + ex.getMessage());
			}
		});
		return kafkaDto;
	}
}
