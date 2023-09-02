package com.example.vote_service.service.impl;

import com.example.vote_service.UserDto;
import com.example.vote_service.domain.dto.*;
import com.example.vote_service.exception.VoteException;
import com.example.vote_service.exception.VoteExceptionCode;
import com.example.vote_service.messagequeue.KafkaProducer;
import com.example.vote_service.messagequeue.MessageProduceResult;
import com.example.vote_service.repository.agenda.AgendaCustomRepository;
import com.example.vote_service.repository.select_option.SelectOptionCustomRepository;
import com.example.vote_service.repository.vote.VoteCustomRepository;
import com.example.vote_service.service.VoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class VoteServiceImpl implements VoteService {
	private final AgendaCustomRepository agendaCustomRepository;
	private final SelectOptionCustomRepository selectOptionRepository;
	private final VoteCustomRepository voteRepository;
	private final KafkaProducer kafkaProducer;

	public VoteServiceImpl(AgendaCustomRepository agendaCustomRepository,
						   SelectOptionCustomRepository selectOptionRepository,
						   VoteCustomRepository voteRepository,
						   KafkaProducer kafkaProducer) {
		this.agendaCustomRepository = agendaCustomRepository;
		this.selectOptionRepository = selectOptionRepository;
		this.voteRepository = voteRepository;
		this.kafkaProducer = kafkaProducer;
	}

	@Override
	@Transactional
	public Mono<Boolean> createVote(VoteCreationDto voteCreationDto) {
		return Mono.just(voteCreationDto)
				.flatMap(creationDto -> selectOptionRepository.findById(creationDto.selectOptionId()))
				.switchIfEmpty(Mono.defer(() -> Mono.error(new VoteException(VoteExceptionCode.SELECT_OPTION_NOT_FOUND))))
				.flatMap(selectOption -> agendaCustomRepository.findApartmentCodeById(selectOption.agendaId()))
				.zipWith(Mono.deferContextual(contextView -> Mono.just((UserDto) contextView.get("user"))))
				.flatMap(tuple -> {
					String selectOptionApartmentCode = tuple.getT1();
					String userApartmentCode = tuple.getT2().apartmentCode();
					if (!selectOptionApartmentCode.equals(userApartmentCode)) {
						return Mono.error(new VoteException(VoteExceptionCode.NO_RIGHT_FOR_VOTE));
					} else {
						return Mono.just(tuple.getT2());
					}
				})
				.map(userDto -> VoteEvent.toEvent(voteCreationDto, userDto.id()))
				.flatMap(kafkaProducer::send)
				.map(MessageProduceResult::getStatus);
		}

	@Override
	@Transactional(readOnly = true)
	public Mono<VoteHistory> getVoteHistory(Long agendaId) {

		return Mono.deferContextual(contextView -> {
			UserDto userDto = contextView.get("user");
			return voteRepository.findVoteHistoryByUserIdAndAgendaId(userDto.id(), agendaId);
		}).switchIfEmpty(Mono.just(new VoteHistory(null,null)));
	}
}
