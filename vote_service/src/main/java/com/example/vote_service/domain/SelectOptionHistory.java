package com.example.vote_service.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;


@Getter
@Entity
public class SelectOptionHistory extends BaseEntity {
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
	public SelectOptionHistory(Long agendaId, String summary, String details, Integer count) {
		this.agendaId = agendaId;
		this.summary = summary;
		this.details = details;
		this.count = count;
	}
}
