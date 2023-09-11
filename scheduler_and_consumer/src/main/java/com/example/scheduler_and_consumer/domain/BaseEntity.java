package com.example.scheduler_and_consumer.domain;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@NoArgsConstructor
public abstract class BaseEntity {
	@CreationTimestamp
	private LocalDateTime createdTime;
	@UpdateTimestamp
	private LocalDateTime updatedTime;

	protected BaseEntity(LocalDateTime createdTime, LocalDateTime updatedTime) {
		this.createdTime = createdTime;
		this.updatedTime = updatedTime;
	}
}
