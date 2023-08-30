package com.example.vote_service.domain.dto;

public record VoteCreationDto(Long agendaId, Long selectOptionId, Long userId) {
}
