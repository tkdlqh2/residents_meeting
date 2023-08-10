package com.example.residents_meeting.vote.repository;

import com.example.residents_meeting.vote.domain.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
}
