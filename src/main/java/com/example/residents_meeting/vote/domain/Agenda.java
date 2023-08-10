package com.example.residents_meeting.vote.domain;

import com.example.residents_meeting.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
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
}
