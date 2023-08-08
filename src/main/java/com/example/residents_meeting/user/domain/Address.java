package com.example.residents_meeting.user.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
record Address(@NotBlank String apartment_code, int building, int unit){
}