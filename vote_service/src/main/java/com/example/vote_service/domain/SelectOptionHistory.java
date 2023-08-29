package com.example.vote_service.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class SelectOptionHistory {
	@Id
	@JsonIgnore
	private Long id;
	@Column(nullable = false)
	private String summary;
	private String details;
	private int count;

	protected SelectOptionHistory() {
	}

	public SelectOptionHistory(String summary, String details, int count) {
		this.summary = summary;
		this.details = details;
		this.count = count;
	}
}
