package com.example.residents_meeting.vote.domain.dto;

import java.time.LocalDate;
import java.util.List;

public record AgendaCreationResultDTO(String apartmentCode, String title, String details, LocalDate endDate, List<String> selectOptionSummaryList) {
}
