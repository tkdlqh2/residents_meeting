package com.example.residents_meeting.vote.repository;

import com.example.residents_meeting.vote.domain.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {
	Optional<LocalDate> findEndDateById(Long id);
	@Query("select a from Agenda a join fetch a.selectOptions where a.id = :id")
	Optional<Agenda> findByIdUsingFetchJoin(Long id);
}
