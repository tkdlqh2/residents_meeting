package com.example.vote_service.repository.agenda;

import com.example.vote_service.domain.Agenda;
import reactor.core.publisher.Mono;

public interface AgendaCustomRepository {
	Mono<Agenda> findByIdUsingFetchJoin(Long id);
}
