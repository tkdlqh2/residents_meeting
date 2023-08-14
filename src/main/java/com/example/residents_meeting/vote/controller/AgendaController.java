package com.example.residents_meeting.vote.controller;

import com.example.residents_meeting.vote.domain.AgendaHistory;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationDTO;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationResultDTO;
import com.example.residents_meeting.vote.service.AgendaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

	@GetMapping("/{agendaId}")
	public ResponseEntity<AgendaHistory> getAgendaHistory(@PathVariable Long agendaId) {
		return ResponseEntity.ok(agendaService.getAgendaHistory(agendaId));
	}
}
