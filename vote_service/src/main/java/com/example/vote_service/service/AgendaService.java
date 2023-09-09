package com.example.vote_service.service;

import com.example.vote_service.domain.AgendaHistory;
import com.example.vote_service.domain.dto.AgendaCreationDTO;
import reactor.core.publisher.Mono;

public interface AgendaService {

	Mono<Boolean> createAgenda(AgendaCreationDTO creationDTO);

	Mono<AgendaHistory> getAgendaHistory(Long agendaId);
}
