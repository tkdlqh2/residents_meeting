package com.example.residents_meeting.vote.service;

import com.example.residents_meeting.vote.domain.AgendaHistory;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationDTO;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationResultDTO;

import java.util.List;

public interface AgendaService {

	AgendaCreationResultDTO createAgenda(AgendaCreationDTO creationDTO);
	AgendaHistory getAgendaHistory(Long agendaId);
	List<Long> getListOfUserIdOfSelectOptionId(Long selectOptionId);
}
