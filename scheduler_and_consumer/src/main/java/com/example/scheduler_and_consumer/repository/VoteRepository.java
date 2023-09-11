package com.example.scheduler_and_consumer.repository;

import com.example.scheduler_and_consumer.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
