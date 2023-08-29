package com.example.vote_service.service;

import com.example.vote_service.domain.AgendaHistory;
import com.example.vote_service.domain.dto.AgendaCreationDTO;
import com.example.vote_service.domain.dto.AgendaCreationResultDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AgendaService {

	Mono<AgendaCreationResultDTO> createAgenda(AgendaCreationDTO creationDTO);

	Flux<AgendaHistory> getAgendaHistory(Long agendaId);

	Flux<List<Long>> getListOfUserIdOfAgendaAndSelectOption(Long agendaId, Long selectOptionId);
}
