package com.example.scheduler_and_consumer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

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
	@ElementCollection
	@CollectionTable(name = "VOTER_IDS", joinColumns = @JoinColumn(name = "select_option_history_id"))
	private List<Long> voterIds;

	protected SelectOptionHistory() {
	}

	@Builder
	private SelectOptionHistory(Long id,
								Long agendaId,
								String summary,
								String details,
								Integer count,
								List<Long> voterIds,
								LocalDateTime createdAt) {
		super(createdAt);
		this.id = id;
		this.agendaId = agendaId;
		this.summary = summary;
		this.details = details;
		this.count = count;
		this.voterIds = voterIds;
	}

	public static SelectOptionHistory from(SelectOption s, Integer count, List<Long> voterIds){
		return SelectOptionHistory.builder()
				.id(s.getId())
				.agendaId(s.getAgenda().getId())
				.summary(s.getSummary())
				.details(s.getDetails())
				.count(count)
				.voterIds(voterIds)
				.createdAt(s.getCreatedAt())
				.build();
	}
}
