package com.example.residents_meeting.vote.domain;

import com.example.residents_meeting.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
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

}
