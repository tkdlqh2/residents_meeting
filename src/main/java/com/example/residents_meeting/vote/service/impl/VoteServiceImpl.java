package com.example.residents_meeting.vote.service.impl;

import com.example.residents_meeting.common.RequestContextHolder;
import com.example.residents_meeting.user.domain.User;
import com.example.residents_meeting.vote.domain.SelectOption;
import com.example.residents_meeting.vote.domain.dto.VoteCreationDto;
import com.example.residents_meeting.vote.domain.dto.VoteCreationResultDto;
import com.example.residents_meeting.vote.domain.dto.VoteEvent;
import com.example.residents_meeting.vote.repository.SelectOptionRepository;
import com.example.residents_meeting.vote.service.VoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteServiceImpl implements VoteService {

	private final RequestContextHolder requestContextHolder;
	private final SelectOptionRepository selectOptionRepository;

	public VoteServiceImpl(RequestContextHolder requestContextHolder, SelectOptionRepository selectOptionRepository) {
		this.requestContextHolder = requestContextHolder;
		this.selectOptionRepository = selectOptionRepository;
	}

	@Override
	@Transactional
	public VoteCreationResultDto createVote(VoteCreationDto voteCreationDto) {
		SelectOption selectOption = selectOptionRepository.findByAgendaIdAndId(
				voteCreationDto.agendaId(),
				voteCreationDto.selectOptionId()
				)
				.orElseThrow(() -> new RuntimeException("Select option not found"));

		User user = requestContextHolder.getUser();
		if (!user.getAddress().apartmentCode().equals(selectOption.getAgenda().getApartmentCode())) {
			throw new RuntimeException("User not in apartment");
		}

		VoteEvent voteEvent = new VoteEvent(selectOption.getId(), user.getId());
		return new VoteCreationResultDto(selectOption.getAgenda().getTitle(), selectOption.getSummary());
	}
}
