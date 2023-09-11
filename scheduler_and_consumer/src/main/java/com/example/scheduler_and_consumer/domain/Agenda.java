package com.example.scheduler_and_consumer.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
}
