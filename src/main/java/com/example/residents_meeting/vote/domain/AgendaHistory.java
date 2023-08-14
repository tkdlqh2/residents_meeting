package com.example.residents_meeting.vote.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

// 기록을 담은 사실상 record 에 가까운 entity
@Getter
@Entity
@Table(name = "AGENDA_HISTORY")
public class AgendaHistory {
	@Id
	private Long id;
	@Column(nullable = false)
	private String title;
	@Column
	@Lob
	private String details;
	@Temporal(TemporalType.DATE)
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;

	@OneToMany
	List<SelectOptionHistory> selectOptions;

	@Builder
	public AgendaHistory(String title, String details, LocalDate endDate, List<SelectOptionHistory> selectOptions) {
		this.title = title;
		this.details = details;
		this.endDate = endDate;
		this.selectOptions = selectOptions;
	}

	protected AgendaHistory() {}

	@Entity
	@Getter
	public static class SelectOptionHistory {
		@Id
		private Long id;
		private String summary;
		private String details;
		private int count;

		public SelectOptionHistory(String summary, String details, int count) {
			this.summary = summary;
			this.details = details;
			this.count = count;
		}

		public SelectOptionHistory() {
		}
	}
}
