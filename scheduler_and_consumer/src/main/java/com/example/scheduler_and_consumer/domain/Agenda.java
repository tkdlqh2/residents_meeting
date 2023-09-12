package com.example.scheduler_and_consumer.domain;

import com.example.scheduler_and_consumer.domain.dto.AgendaPayload;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

	@Column
	private boolean secret;

	@Builder
	public Agenda(LocalDateTime createdTime,
				  Long id,
				  String apartmentCode,
				  String title,
				  String details,
				  LocalDate endDate,
				  boolean secret,
				  List<SelectOption> selectOptions) {
		super(createdTime);
		this.id = id;
		this.apartmentCode = apartmentCode;
		this.title = title;
		this.details = details;
		this.endDate = endDate;
		this.secret = secret;
		this.selectOptions = selectOptions;
	}

	@OneToMany(mappedBy = "agenda", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	private List<SelectOption> selectOptions;

	public static Agenda from(AgendaPayload payload) {
		return Agenda.builder()
				.apartmentCode(payload.apartmentCode())
				.title(payload.title())
				.details(payload.details())
				.endDate(payload.endDate())
				.secret(payload.secret())
				.createdTime(payload.createdAt())
				.build();
	}
}
