package com.example.scheduler_and_consumer.domain;

import com.example.scheduler_and_consumer.domain.dto.VotePayload;
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

	private Vote(Long selectOptionId, Long userId) {
		this.selectOptionId = selectOptionId;
		this.userId = userId;
	}

	public static Vote from(VotePayload payload) {
		return new Vote(payload.selectOptionId(), payload.userId());
	}

	public Vote() {
	}
}
