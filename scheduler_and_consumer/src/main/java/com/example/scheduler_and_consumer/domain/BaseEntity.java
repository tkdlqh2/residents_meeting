package com.example.scheduler_and_consumer.domain;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@NoArgsConstructor
public abstract class BaseEntity {
	private LocalDateTime createdAt;

	protected BaseEntity(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
