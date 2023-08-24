package com.example.residents_meeting.vote.service.impl;

import com.example.residents_meeting.common.RequestContextHolder;
import com.example.residents_meeting.common.config.messagequeue.KafkaProducer;
import com.example.residents_meeting.user.domain.User;
import com.example.residents_meeting.vote.domain.SelectOption;
import com.example.residents_meeting.vote.domain.dto.VoteCreationDto;
import com.example.residents_meeting.vote.domain.dto.VoteCreationResultDto;
import com.example.residents_meeting.vote.domain.dto.VoteEvent;
import com.example.residents_meeting.vote.domain.dto.VoteHistory;
import com.example.residents_meeting.vote.exception.VoteException;
import com.example.residents_meeting.vote.exception.VoteExceptionCode;
import com.example.residents_meeting.vote.repository.SelectOptionRepository;
import com.example.residents_meeting.vote.repository.VoteRepository;
import com.example.residents_meeting.vote.service.VoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteServiceImpl implements VoteService {

	private final RequestContextHolder requestContextHolder;
	private final SelectOptionRepository selectOptionRepository;
	private final VoteRepository voteRepository;
	private final KafkaProducer kafkaProducer;

	public VoteServiceImpl(RequestContextHolder requestContextHolder,
						   SelectOptionRepository selectOptionRepository,
						   VoteRepository voteRepository,
						   KafkaProducer kafkaProducer) {
		this.requestContextHolder = requestContextHolder;
		this.selectOptionRepository = selectOptionRepository;
		this.voteRepository = voteRepository;
		this.kafkaProducer = kafkaProducer;
	}

	@Override
	@Transactional
	public VoteCreationResultDto createVote(VoteCreationDto voteCreationDto) {
		SelectOption selectOption = selectOptionRepository.findByAgendaIdAndId(
				voteCreationDto.agendaId(),
				voteCreationDto.selectOptionId())
				.orElseThrow(() -> new VoteException(VoteExceptionCode.SELECT_OPTION_NOT_FOUND));

		User user = requestContextHolder.getUser();
		if (!user.getAddress().getApartmentCode().equals(selectOption.getAgenda().getApartmentCode())) {
			throw new VoteException(VoteExceptionCode.NO_RIGHT_FOR_VOTE);
		}

		VoteEvent voteEvent = new VoteEvent(selectOption.getId(), user.getId());
		kafkaProducer.send(voteEvent);
		return new VoteCreationResultDto(selectOption.getAgenda().getTitle(), selectOption.getSummary());
	}

	@Override
	@Transactional(readOnly = true)
	public VoteHistory getVoteHistory(Long agendaId) {
		return voteRepository.findVoteHistoryByUserIdAndAgendaId(
				requestContextHolder.getUser().getId(),
				agendaId);
	}
}
