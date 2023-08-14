package com.example.residents_meeting.vote.service.impl;

import com.example.residents_meeting.vote.domain.Agenda;
import com.example.residents_meeting.vote.domain.SelectOption;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationDTO;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationResultDTO;
import com.example.residents_meeting.vote.repository.AgendaRepository;
import com.example.residents_meeting.vote.repository.SelectOptionRepository;
import com.example.residents_meeting.vote.service.AgendaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AgendaServiceImpl implements AgendaService {

	private final AgendaRepository agendaRepository;
	private final SelectOptionRepository selectOptionRepository;

	public AgendaServiceImpl(AgendaRepository agendaRepository, SelectOptionRepository selectOptionRepository) {
		this.agendaRepository = agendaRepository;
		this.selectOptionRepository = selectOptionRepository;
	}

	@Override
	@Transactional
	public AgendaCreationResultDTO createAgenda(AgendaCreationDTO creationDTO) {
		Agenda agenda = Agenda.from(creationDTO);
		agendaRepository.save(agenda);

		List<SelectOption> selectOptions = creationDTO.selectOptionCreationDtoList()
				.stream()
				.map(selectOptionCreationDto -> SelectOption.from(agenda, selectOptionCreationDto))
				.toList();
		selectOptionRepository.saveAll(selectOptions);

		agenda.setSelectOptions(selectOptions);
		return agenda.toAgendaCreationResultDTO();
	}
}
