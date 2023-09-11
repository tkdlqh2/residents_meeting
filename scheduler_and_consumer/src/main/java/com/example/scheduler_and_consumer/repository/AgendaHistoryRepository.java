package com.example.scheduler_and_consumer.repository;

import com.example.scheduler_and_consumer.domain.AgendaHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaHistoryRepository extends JpaRepository<AgendaHistory,Long> {
}
