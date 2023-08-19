package com.example.residents_meeting.vote.controller;

import com.example.residents_meeting.vote.domain.dto.VoteCreationDto;
import com.example.residents_meeting.vote.domain.dto.VoteCreationResultDto;
import com.example.residents_meeting.vote.domain.dto.VoteHistory;
import com.example.residents_meeting.vote.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vote")
public class VoteController {
	private final VoteService voteService;

	public VoteController(VoteService voteService) {
		this.voteService = voteService;
	}

	@PostMapping("/")
	public ResponseEntity<VoteCreationResultDto> vote(@RequestBody @Validated VoteCreationDto creationDto) {
		return ResponseEntity.ok(voteService.createVote(creationDto));
	}

	@GetMapping("/{agendaId}")
	public ResponseEntity<VoteHistory> getCurrentVote(@PathVariable Long agendaId) {
		return ResponseEntity.ok(voteService.getVoteHistory(agendaId));
	}
}
