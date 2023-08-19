package com.example.residents_meeting.vote.controller;

import com.example.residents_meeting.vote.domain.AgendaHistory;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationDTO;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationResultDTO;
import com.example.residents_meeting.vote.service.AgendaService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agenda")
public class AgendaController {

	private final AgendaService agendaService;

	public AgendaController(AgendaService agendaService) {
		this.agendaService = agendaService;
	}

	@PostMapping("/")
	public ResponseEntity<AgendaCreationResultDTO> createAgenda(
			@RequestBody @Validated AgendaCreationDTO creationDTO) {
		return ResponseEntity.ok(agendaService.createAgenda(creationDTO));
	}

	@GetMapping("/{agendaId}")
	public ResponseEntity<AgendaHistory> getAgendaHistory(@PathVariable Long agendaId) {
		return ResponseEntity.ok(agendaService.getAgendaHistory(agendaId));
	}

	@GetMapping("{agendaId}/select-option/{selectOptionId}")
	public ResponseEntity<List<Long>> getListOfUserOfSelectOption(@PathVariable Long agendaId, @PathVariable Long selectOptionId) {

		//TODO: get list of user info from list of user id
		return ResponseEntity.ok(agendaService.getListOfUserIdOfAgendaAndSelectOption(agendaId, selectOptionId));
	}
}
