package com.example.residents_meeting.vote.service.impl;

import com.example.residents_meeting.vote.domain.Agenda;
import com.example.residents_meeting.vote.domain.AgendaHistory;
import com.example.residents_meeting.vote.domain.SelectOption;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationDTO;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationResultDTO;
import com.example.residents_meeting.vote.exception.VoteException;
import com.example.residents_meeting.vote.exception.VoteExceptionCode;
import com.example.residents_meeting.vote.repository.AgendaHistoryRepository;
import com.example.residents_meeting.vote.repository.AgendaRepository;
import com.example.residents_meeting.vote.repository.SelectOptionRepository;
import com.example.residents_meeting.vote.service.AgendaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AgendaServiceImpl implements AgendaService {

	private final AgendaRepository agendaRepository;
	private final SelectOptionRepository selectOptionRepository;
	private final AgendaHistoryRepository agendaHistoryRepository;

	public AgendaServiceImpl(AgendaRepository agendaRepository,
							 SelectOptionRepository selectOptionRepository,
							 AgendaHistoryRepository agendaHistoryRepository) {
		this.agendaRepository = agendaRepository;
		this.selectOptionRepository = selectOptionRepository;
		this.agendaHistoryRepository = agendaHistoryRepository;
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

	@Transactional(readOnly = true)
	@Override
	public AgendaHistory getAgendaHistory(Long agendaId) {
		LocalDate endDate = agendaRepository.findEndDateById(agendaId)
				.orElseThrow(() -> new VoteException(VoteExceptionCode.AGENDA_NOT_FOUND));

		if (LocalDate.now().isBefore(endDate)) {
			throw new VoteException(VoteExceptionCode.ONGOING_SECRET_VOTE);
		}

		return agendaHistoryRepository.findById(agendaId)
				.orElseGet( () -> {
					Agenda agenda = agendaRepository.findByIdUsingFetchJoin(agendaId)
							.orElseThrow(() -> new VoteException(VoteExceptionCode.AGENDA_NOT_FOUND));

					return AgendaHistory.builder()
							.title(agenda.getTitle())
							.details(agenda.getDetails())
							.endDate(agenda.getEndDate())
							.selectOptions(
									agenda.getSelectOptions().stream()
									.map(selectOption -> new AgendaHistory.SelectOptionHistory(
											selectOption.getSummary(),
											selectOption.getDetails(),
											selectOptionRepository.countById(selectOption.getId())
										)
									).toList())
							.build();
					});
	}
}
