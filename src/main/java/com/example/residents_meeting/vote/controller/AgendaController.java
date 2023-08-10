package com.example.residents_meeting.vote.controller;

import com.example.residents_meeting.vote.domain.dto.AgendaCreationDTO;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationResultDTO;
import com.example.residents_meeting.vote.service.AgendaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agenda")
public class AgendaController {

	private final AgendaService agendaService;

	public AgendaController(AgendaService agendaService) {
		this.agendaService = agendaService;
	}

	@PostMapping("/")
	public ResponseEntity<AgendaCreationResultDTO> createAgenda(
			@RequestBody AgendaCreationDTO creationDTO) {
		return ResponseEntity.ok(agendaService.createAgenda(creationDTO));
	}
}
