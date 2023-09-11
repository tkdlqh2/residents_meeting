package com.example.scheduler_and_consumer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
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
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer count;

	protected SelectOptionHistory() {
	}

	@Builder
	public SelectOptionHistory(String summary, String details, Integer count) {
		this.summary = summary;
		this.details = details;
		this.count = count;
	}
}
