package com.example.residents_meeting.vote.domain;

import com.example.residents_meeting.common.BaseEntity;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Agenda extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String apartmentCode;

	@Column(nullable = false)
	private String title;

	@Column
	@Lob
	private String details;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private LocalDate endDate;

	@OneToMany(mappedBy = "agenda", orphanRemoval = true)
	private List<SelectOption> selectOptions;

	private Agenda(String apartmentCode,
				   String title,
				   String details,
				   LocalDate endDate,
				   List<SelectOption> selectOptions) {
		this.apartmentCode = apartmentCode;
		this.title = title;
		this.details = details;
		this.endDate = endDate;
		this.selectOptions = selectOptions;
	}

	public static Agenda from(AgendaCreationDTO creationDTO) {
		return new Agenda(creationDTO.apartmentCode(),
				creationDTO.title(),
				creationDTO.details(),
				creationDTO.endDate(),
				creationDTO.selectOptionCreationDtoList()
						.stream().map(SelectOption::from)
						.toList());
	}
}
