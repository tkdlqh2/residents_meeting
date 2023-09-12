package com.example.vote_service.domain;

import com.example.vote_service.domain.dto.SelectOptionCreationDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectOption extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "agenda_id", nullable = false)
	private Long agendaId;

	@Column(nullable = false)
	private String summary;

	@Column
	@Lob
	private String details;

	@Builder
	protected SelectOption(Long id, Long agendaId, String summary, String details) {
		this.id = id;
		this.agendaId = agendaId;
		this.summary = summary;
		this.details = details;
	}

	public static SelectOption from(Agenda agenda, SelectOptionCreationDto creationDto) {
		return new SelectOption(null, agenda.getId(), creationDto.summary(), creationDto.details());
	}
}
