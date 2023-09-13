package com.example.vote_service.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
public class SelectOptionHistory extends BaseEntity {
	@JsonIgnore
	private Long id;
	private Long agendaId;
	private String summary;
	private String details;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer count;
	private List<Long> voterIds;

	@Builder
	public SelectOptionHistory(Long agendaId, String summary, String details, Integer count) {
		this.agendaId = agendaId;
		this.summary = summary;
		this.details = details;
		this.count = count;
	}
}
