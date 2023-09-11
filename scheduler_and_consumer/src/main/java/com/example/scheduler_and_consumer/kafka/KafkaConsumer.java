package com.example.scheduler_and_consumer.kafka;

import com.example.scheduler_and_consumer.domain.Agenda;
import com.example.scheduler_and_consumer.domain.Vote;
import com.example.scheduler_and_consumer.repository.AgendaRepository;
import com.example.scheduler_and_consumer.repository.VoteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class KafkaConsumer {

	private final ObjectMapper mapper = new ObjectMapper();
	private final AgendaRepository agendaRepository;
	private final VoteRepository voteRepository;

	public KafkaConsumer(AgendaRepository agendaRepository, VoteRepository voteRepository) {
		this.agendaRepository = agendaRepository;
		this.voteRepository = voteRepository;
	}

	@KafkaListener(topics = "agenda_sink")
	public void consumeAgendaEvent(String message) {
		log.info("Consumed message: {}", message);

		try {
			Map<Object, Object> map = mapper.readValue(message, new TypeReference<>() {});
			Agenda agenda = mapper.readValue(((String) map.get("requestedMessage")), Agenda.class);
			agendaRepository.save(agenda);
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
		}
	}

	@KafkaListener(topics = "vote_sink")
	public void consumeVoteEvent(String message) {
		log.info("Consumed message: {}", message);

		try {
			Map<Object, Object> map = mapper.readValue(message, new TypeReference<>() {});
			Vote vote = mapper.readValue(((String) map.get("requestedMessage")), Vote.class);
			voteRepository.save(vote);
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
		}
	}
}
