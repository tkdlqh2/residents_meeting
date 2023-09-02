package com.example.vote_service.controller;

import com.example.vote_service.domain.dto.VoteCreationDto;
import com.example.vote_service.domain.dto.VoteHistory;
import com.example.vote_service.service.VoteService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/vote")
public class VoteController {
	private final VoteService voteService;

	public VoteController(VoteService voteService) {
		this.voteService = voteService;
	}

	@PostMapping("/")
	public Mono<Boolean> vote(@RequestBody @Validated VoteCreationDto creationDto) {
		return voteService.createVote(creationDto);
	}

	@GetMapping("/{agendaId}")
	public Mono<VoteHistory> getCurrentVote(@PathVariable Long agendaId) {
		return voteService.getVoteHistory(agendaId);
	}
}
