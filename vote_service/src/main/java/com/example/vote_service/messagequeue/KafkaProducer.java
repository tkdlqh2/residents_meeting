package com.example.vote_service.messagequeue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
	private final KafkaSender<String, Object> producer;

	public Mono<MessageProduceResult> send(Event message) {

		return producer.createOutbound()
				.send(Mono.just(new ProducerRecord<>(message.getTopicName(), null, message.getRequestedMessage())))
				.then()
				.thenReturn(new MessageProduceResult(message))
				.onErrorResume(e -> Mono.just(new MessageProduceResult(message, e)));
	}
}
