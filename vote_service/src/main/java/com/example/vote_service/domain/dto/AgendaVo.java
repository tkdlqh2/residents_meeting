package com.example.vote_service.domain.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AgendaVo(Long id,
					   String apartmentCode,
					   String title,
					   String details,
					   LocalDate endDate,
					   LocalDateTime createdAt,
					   LocalDateTime updatedAt,
					   List<SelectOptionVo> selectOptionList) {
}
