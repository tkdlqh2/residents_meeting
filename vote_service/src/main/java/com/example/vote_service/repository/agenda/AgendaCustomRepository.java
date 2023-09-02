package com.example.vote_service.repository.agenda;

import com.example.vote_service.domain.dto.AgendaVo;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface AgendaCustomRepository {
	Mono<AgendaVo> findByIdUsingFetchJoin(Long id);

	Mono<LocalDate> findEndDateById(Long agendaId);

	Mono<String> findApartmentCodeById(Long agendaId);
}
