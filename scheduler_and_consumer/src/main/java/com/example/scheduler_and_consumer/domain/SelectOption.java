package com.example.scheduler_and_consumer.domain;

import com.example.scheduler_and_consumer.domain.dto.AgendaPayload;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectOption extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Agenda agenda;

	@Column(nullable = false)
	private String summary;

	@Column
	@Lob
	private String details;

	private SelectOption(Long id, Agenda agenda, String summary, String details, LocalDateTime createdAt) {
		super(createdAt);
		this.id = id;
		this.agenda = agenda;
		this.summary = summary;
		this.details = details;
	}

	public static SelectOption from(Agenda agenda, AgendaPayload.SelectOptionPayload payload) {
		return new SelectOption(null, agenda, payload.summary(), payload.details(), payload.createdAt());
	}
}
