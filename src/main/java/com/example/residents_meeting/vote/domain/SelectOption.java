package com.example.residents_meeting.vote.domain;

import com.example.residents_meeting.common.BaseEntity;
import com.example.residents_meeting.vote.domain.dto.SelectOptionCreationDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectOption extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "agenda_id")
	private Agenda agenda;

	@Column(nullable = false)
	private String summary;

	@Column
	@Lob
	private String details;

	@OneToMany(mappedBy = "selectOption", orphanRemoval = true)
	private List<Vote> votes;

	private SelectOption(String summary, String details) {
		this.summary = summary;
		this.details = details;
	}

	public static SelectOption from(SelectOptionCreationDto creationDto) {
		return new SelectOption(creationDto.summary(), creationDto.details());
	}

}
