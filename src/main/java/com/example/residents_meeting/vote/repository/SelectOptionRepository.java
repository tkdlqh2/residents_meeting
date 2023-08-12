package com.example.residents_meeting.vote.repository;

import com.example.residents_meeting.vote.domain.SelectOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SelectOptionRepository extends JpaRepository<SelectOption, Long> {
	Optional<SelectOption> findByAgendaIdAndId(Long agendaId, Long id);
}
