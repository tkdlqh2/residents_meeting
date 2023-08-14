package com.example.residents_meeting.vote.controller;

import com.example.residents_meeting.vote.domain.dto.VoteCreationDto;
import com.example.residents_meeting.vote.domain.dto.VoteCreationResultDto;
import com.example.residents_meeting.vote.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vote")
public class VoteController {
	private final VoteService voteService;

	public VoteController(VoteService voteService) {
		this.voteService = voteService;
	}

	@PostMapping("/")
	public ResponseEntity<VoteCreationResultDto> vote(@RequestBody VoteCreationDto creationDto) {
		return ResponseEntity.ok(voteService.createVote(creationDto));
	}
}
