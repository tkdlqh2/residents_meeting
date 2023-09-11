package com.example.scheduler_and_consumer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Entity
@Table(name = "AGENDA_HISTORY")
public class AgendaHistory extends BaseEntity {

	@Id
	@JsonIgnore
	private Long id;
	@Column
	private String apartmentCode;
	@Column
	private String title;
	@Column
	@Lob
	private String details;
	@Temporal(TemporalType.DATE)
	@Column(name = "end_date")
	private LocalDate endDate;

	@OneToMany
	@JoinColumn(name = "agenda_id")
	List<SelectOptionHistory> selectOptions;

	@Builder
	public AgendaHistory(Long id, String apartmentCode, String title, String details, LocalDate endDate, List<SelectOptionHistory> selectOptions) {
		this.id = id;
		this.apartmentCode = apartmentCode;
		this.title = title;
		this.details = details;
		this.endDate = endDate;
		this.selectOptions = selectOptions;
	}

	protected AgendaHistory() {
	}
}
