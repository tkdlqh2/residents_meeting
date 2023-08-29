package com.example.vote_service.domain;

import jakarta.persistence.*;
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
