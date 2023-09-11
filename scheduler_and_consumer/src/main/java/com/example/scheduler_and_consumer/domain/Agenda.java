package com.example.scheduler_and_consumer.domain;

import com.example.scheduler_and_consumer.domain.dto.AgendaPayload;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Entity
@Getter
@AllArgsConstructor
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

	@Column
	private boolean secret;

	@OneToMany(mappedBy = "agenda")
	private List<SelectOption> selectOptions;

	public static Agenda from(AgendaPayload payload) {
		return Agenda.builder()
				.apartmentCode(payload.apartmentCode())
				.title(payload.title())
				.details(payload.details())
				.endDate(payload.endDate())
				.secret(payload.secret())
				.build();
	}
}
