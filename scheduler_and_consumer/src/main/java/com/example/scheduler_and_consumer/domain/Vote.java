package com.example.scheduler_and_consumer.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Vote extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long selectOptionId;

	private Long userId;

	public Vote(Long selectOptionId, Long userId) {
		this.selectOptionId = selectOptionId;
		this.userId = userId;
	}

	public Vote() {
	}
}
