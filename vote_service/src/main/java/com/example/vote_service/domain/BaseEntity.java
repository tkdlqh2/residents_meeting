package com.example.vote_service.domain;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@NoArgsConstructor
public abstract class BaseEntity {
	@CreationTimestamp
	private LocalDateTime createdAt;

	protected BaseEntity(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
