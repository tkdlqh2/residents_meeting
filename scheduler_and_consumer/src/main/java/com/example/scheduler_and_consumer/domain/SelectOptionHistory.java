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
	private Long agendaId;
	@Column(nullable = false)
	private String summary;
	private String details;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer count;

	protected SelectOptionHistory() {
	}

	@Builder
	private SelectOptionHistory(Long id, Long agendaId, String summary, String details, Integer count) {
		this.id = id;
		this.agendaId = agendaId;
		this.summary = summary;
		this.details = details;
		this.count = count;
	}

	public static SelectOptionHistory from(SelectOption s, Integer count){
		return SelectOptionHistory.builder()
				.id(s.getId())
				.agendaId(s.getAgenda().getId())
				.summary(s.getSummary())
				.details(s.getDetails())
				.count(count)
				.build();
	}
}
