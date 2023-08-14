package com.example.residents_meeting.vote.service;

import com.example.residents_meeting.vote.domain.dto.VoteCreationDto;
import com.example.residents_meeting.vote.domain.dto.VoteCreationResultDto;
import com.example.residents_meeting.vote.domain.dto.VoteHistory;

public interface VoteService {
	VoteCreationResultDto createVote(VoteCreationDto voteCreationDto);

	VoteHistory getVoteHistory(Long agendaId);
}
