package com.example.residents_meeting.vote.service;

import com.example.residents_meeting.vote.domain.dto.AgendaCreationDTO;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationResultDTO;

public interface AgendaService {

	AgendaCreationResultDTO createAgenda(AgendaCreationDTO creationDTO);
}
