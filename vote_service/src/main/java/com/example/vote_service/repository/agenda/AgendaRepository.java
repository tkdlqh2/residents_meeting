package com.example.vote_service.repository.agenda;

import com.example.vote_service.domain.Agenda;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface AgendaRepository extends R2dbcRepository<Agenda, Long> {
	@Query("select a.endDate from Agenda a where a.id = :id")
	Mono<LocalDate> findEndDateById(Long id);
}
