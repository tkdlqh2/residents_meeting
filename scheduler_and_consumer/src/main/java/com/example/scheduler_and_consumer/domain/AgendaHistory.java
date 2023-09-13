package com.example.scheduler_and_consumer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "AGENDA_HISTORY")
public class AgendaHistory extends BaseEntity {

	@Id
	@JsonIgnore
	private Long id;
	@Column(name = "apartment_code")
	private String apartmentCode;
	@Column
	private String title;
	@Column
	@Lob
	private String details;
	@Column
	private Boolean secret;
	@Temporal(TemporalType.DATE)
	@Column(name = "end_date")
	private LocalDate endDate;

	@Builder
	private AgendaHistory(Long id, String apartmentCode, String title, String details, Boolean secret, LocalDate endDate, LocalDateTime createdAt) {
		super(createdAt);
		this.id = id;
		this.apartmentCode = apartmentCode;
		this.title = title;
		this.details = details;
		this.secret = secret;
		this.endDate = endDate;
	}

	public static AgendaHistory from(Agenda a){

		return AgendaHistory.builder()
				.id(a.getId())
				.apartmentCode(a.getApartmentCode())
				.title(a.getTitle())
				.details(a.getDetails())
				.endDate(a.getEndDate())
				.secret(a.isSecret())
				.createdAt(a.getCreatedAt())
				.build();
	}

	protected AgendaHistory() {
	}
}
