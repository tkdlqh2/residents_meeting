package com.example.residents_meeting.vote.domain.dto;

import java.time.LocalDateTime;

public record VoteHistory(Long selectOptionId, LocalDateTime voteTime) {
}
