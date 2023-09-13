package com.example.scheduler_and_consumer.repository;

import com.example.scheduler_and_consumer.domain.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
}
