package com.example.residents_meeting.vote.domain;

import com.example.residents_meeting.common.BaseEntity;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationDTO;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationResultDTO;
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

	@OneToMany(mappedBy = "agenda", orphanRemoval = true, cascade = CascadeType.PERSIST)
	private List<SelectOption> selectOptions;

	protected Agenda(String apartmentCode,
				   String title,
				   String details,
				   LocalDate endDate) {
		this.apartmentCode = apartmentCode;
		this.title = title;
		this.details = details;
		this.endDate = endDate;
	}

	public void setSelectOptions(List<SelectOption> selectOptions) {
		this.selectOptions = selectOptions;
	}

	public static Agenda from(AgendaCreationDTO creationDTO) {
		return new Agenda(creationDTO.apartmentCode(),
				creationDTO.title(),
				creationDTO.details(),
				creationDTO.endDate());
	}

	public AgendaCreationResultDTO toAgendaCreationResultDTO() {
		return new AgendaCreationResultDTO(apartmentCode,
				title,
				details,
				endDate,
				selectOptions.stream().map(SelectOption::getSummary).toList());
	}

	protected void setId(Long id) {
		this.id = id;
	}
}
