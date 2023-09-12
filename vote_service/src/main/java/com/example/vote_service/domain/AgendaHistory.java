package com.example.vote_service.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.List;

// 기록을 담은 사실상 record 에 가까운 entity
@Getter
@Entity
@Table(name = "AGENDA_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
	@Column(value = "end_date")
	private LocalDate endDate;

	@OneToMany
	@JoinColumn(name = "agenda_id")
	List<SelectOptionHistory> selectOptions;

	@Builder
	protected AgendaHistory(Long id, String apartmentCode, String title, String details, LocalDate endDate, List<SelectOptionHistory> selectOptions) {
		super();
		this.id = id;
		this.apartmentCode = apartmentCode;
		this.title = title;
		this.details = details;
		this.endDate = endDate;
		this.selectOptions = selectOptions;
	}
}
