package com.example.vote_service.domain.dto;

import com.example.vote_service.messagequeue.Event;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AgendaEvent extends Event {

	private AgendaEvent(AgendaPayload agendaPayload) throws JsonProcessingException {
		super("agenda_sink", agendaPayload);
	}

	public static AgendaEvent from(AgendaCreationDTO creationDTO) {
		try {
			return new AgendaEvent(new AgendaPayload(creationDTO));
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	private record AgendaPayload(
			Long Id,
			String apartmentCode,
			String title,
			String details,
			LocalDate endDate,
			LocalDateTime createdAt,
			LocalDateTime updatedAt,
			List<SelectOptionPayload> selectOptionPayloadList

	) {
		public AgendaPayload(AgendaCreationDTO creationDTO) {
			this(null,
					creationDTO.apartmentCode(),
					creationDTO.title(),
					creationDTO.details(),
					creationDTO.endDate(),
					LocalDateTime.now(),
					null,
					creationDTO.selectOptionCreationDtoList().stream()
							.map(SelectOptionPayload::new).toList());
		}

	}

	private record SelectOptionPayload(
			Long id,
			String summary,
			String details
	){
		public SelectOptionPayload(SelectOptionCreationDto creationDto) {
			this(null, creationDto.summary(), creationDto.details());
		}
	}
}
