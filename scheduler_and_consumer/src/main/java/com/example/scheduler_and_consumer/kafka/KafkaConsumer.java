package com.example.scheduler_and_consumer.kafka;

import com.example.scheduler_and_consumer.domain.Agenda;
import com.example.scheduler_and_consumer.domain.SelectOption;
import com.example.scheduler_and_consumer.domain.Vote;
import com.example.scheduler_and_consumer.domain.dto.AgendaPayload;
import com.example.scheduler_and_consumer.domain.dto.VotePayload;
import com.example.scheduler_and_consumer.repository.AgendaRepository;
import com.example.scheduler_and_consumer.repository.SelectOptionRepository;
import com.example.scheduler_and_consumer.repository.VoteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class KafkaConsumer {

	private final ObjectMapper mapper;
	private final AgendaRepository agendaRepository;
	private final SelectOptionRepository selectOptionRepository;
	private final VoteRepository voteRepository;

	public KafkaConsumer(ObjectMapper mapper,
						 AgendaRepository agendaRepository,
						 SelectOptionRepository selectOptionRepository,
						 VoteRepository voteRepository) {
		this.mapper = mapper;
		this.agendaRepository = agendaRepository;
		this.selectOptionRepository = selectOptionRepository;
		this.voteRepository = voteRepository;
	}

	@KafkaListener(topics = "agenda_sink")
	@Transactional
	public void consumeAgendaEvent(String message) {
		log.info("Consumed message: {}", message);

		try {
			AgendaPayload agendaPayload = mapper.readValue(message, AgendaPayload.class);
			Agenda agenda = Agenda.from(agendaPayload);
			Agenda savedAgenda = agendaRepository.save(agenda);

			List<SelectOption> selectOptionList = agendaPayload.selectOptionPayloadList().stream()
					.map(selectOptionPayload -> SelectOption.from(savedAgenda, selectOptionPayload))
					.toList();
			selectOptionRepository.saveAll(selectOptionList);
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
		}
	}

	@KafkaListener(topics = "vote_sink")
	public void consumeVoteEvent(String message) {
		log.info("Consumed message: {}", message);

		try {
			VotePayload votePayload = mapper.readValue(message, VotePayload.class);
			voteRepository.save(Vote.from(votePayload));
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
		}
	}
}
