package com.example.residents_meeting.common;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class BaseEntity {
	@CreationTimestamp
	private LocalDateTime createdTime;
	@UpdateTimestamp
	private LocalDateTime updatedTime;
}
