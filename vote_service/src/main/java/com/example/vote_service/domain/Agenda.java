package com.example.vote_service.domain;

import com.example.vote_service.domain.dto.AgendaCreationDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Agenda extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String apartmentCode;

	@Column
	private String title;

	@Column
	@Lob
	private String details;

	@Column
	@Temporal(TemporalType.DATE)
	private LocalDate endDate;

	@Builder
	public Agenda(Long id,
					 String apartmentCode,
					 String title,
					 String details,
					 LocalDate endDate,
				  	LocalDateTime createdAt,
				  	LocalDateTime updatedAt) {
		super(createdAt, updatedAt);
		this.id = id;
		this.apartmentCode = apartmentCode;
		this.title = title;
		this.details = details;
		this.endDate = endDate;
	}

	public static Agenda from(AgendaCreationDTO creationDTO) {
		return new Agenda(null,
				creationDTO.apartmentCode(),
				creationDTO.title(),
				creationDTO.details(),
				creationDTO.endDate(),
				LocalDateTime.now(),
				null);
	}
}
