package com.example.residents_meeting.user.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public record Address(@NotBlank String apartmentCode, int building, int unit){
	protected Address() {
		this(null, 0, 0);
	}
}