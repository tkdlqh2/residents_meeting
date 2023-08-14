package com.example.residents_meeting.vote.repository;

import com.example.residents_meeting.vote.domain.AgendaHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaHistoryRepository extends JpaRepository<AgendaHistory, Long> {
}
