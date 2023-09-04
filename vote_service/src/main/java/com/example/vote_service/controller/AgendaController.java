package com.example.vote_service.controller;

import com.example.vote_service.domain.AgendaHistory;
import com.example.vote_service.domain.dto.AgendaCreationDTO;
import com.example.vote_service.filter.Authorize;
import com.example.vote_service.service.AgendaService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.example.vote_service.UserInfo.UserRole.*;

@RestController
@RequestMapping("/api/agenda")
public class AgendaController {

	private final AgendaService agendaService;

	public AgendaController(AgendaService agendaService) {
		this.agendaService = agendaService;
	}

	@Authorize(role = VICE_LEADER)
	@PostMapping(value = "/")
	public Mono<Boolean> createAgenda(
			@RequestBody @Validated AgendaCreationDTO creationDTO) {
		return agendaService.createAgenda(creationDTO);
	}

	@Authorize(role = MEMBER)
	@GetMapping(value = "/{agendaId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<AgendaHistory> getAgendaHistory(@PathVariable Long agendaId) {
		return agendaService.getAgendaHistory(agendaId);
	}

	@Authorize(role = MEMBER)
	@GetMapping(value = "{agendaId}/select-option/{selectOptionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<List<Long>> getListOfUserOfSelectOption(@PathVariable Long agendaId, @PathVariable Long selectOptionId) {

		//TODO: get list of user info from list of user id
		return agendaService.getListOfUserIdOfAgendaAndSelectOption(agendaId, selectOptionId);
	}
}
