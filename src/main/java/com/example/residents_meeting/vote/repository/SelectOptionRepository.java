package com.example.residents_meeting.vote.repository;

import com.example.residents_meeting.vote.domain.SelectOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelectOptionRepository extends JpaRepository<SelectOption, Long> {
}
