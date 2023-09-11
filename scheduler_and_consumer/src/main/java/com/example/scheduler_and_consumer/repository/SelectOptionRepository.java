package com.example.scheduler_and_consumer.repository;

import com.example.scheduler_and_consumer.domain.SelectOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelectOptionRepository extends JpaRepository<SelectOption,Long> {
}
