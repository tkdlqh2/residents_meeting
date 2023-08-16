package com.example.residents_meeting.vote.repository;

import com.example.residents_meeting.vote.domain.AgendaHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaHistoryRepository extends JpaRepository<AgendaHistory, Long> {
}
