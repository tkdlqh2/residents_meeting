package com.example.vote_service.service;


import com.example.vote_service.domain.dto.VoteCreationDto;
import com.example.vote_service.domain.dto.VoteCreationResultDto;
import com.example.vote_service.domain.dto.VoteHistory;
import reactor.core.publisher.Mono;

public interface VoteService {
	Mono<VoteCreationResultDto> createVote(VoteCreationDto voteCreationDto);

	Mono<VoteHistory> getVoteHistory(Long agendaId);
}
