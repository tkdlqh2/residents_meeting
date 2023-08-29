package com.example.vote_service.service.impl;

import com.example.vote_service.UserDto;
import com.example.vote_service.domain.dto.*;
import com.example.vote_service.exception.VoteException;
import com.example.vote_service.exception.VoteExceptionCode;
import com.example.vote_service.messagequeue.KafkaProducer;
import com.example.vote_service.repository.select_option.SelectOptionCustomRepository;
import com.example.vote_service.repository.vote.VoteCustomRepository;
import com.example.vote_service.service.VoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class VoteServiceImpl implements VoteService {

	private final SelectOptionCustomRepository selectOptionRepository;
	private final VoteCustomRepository voteRepository;
	private final KafkaProducer kafkaProducer;

	public VoteServiceImpl(SelectOptionCustomRepository selectOptionRepository,
						   VoteCustomRepository voteRepository,
						   KafkaProducer kafkaProducer) {
		this.selectOptionRepository = selectOptionRepository;
		this.voteRepository = voteRepository;
		this.kafkaProducer = kafkaProducer;
	}

	@Override
	@Transactional
	public Mono<VoteCreationResultDto> createVote(VoteCreationDto voteCreationDto) {
		return Mono
				.just(voteCreationDto)
				.flatMap(creationDto -> selectOptionRepository.findById(creationDto.selectOptionId()))
				.zipWith(Mono.deferContextual(contextView -> Mono.just((UserDto) contextView.get("user"))))
				.flatMap(tuple -> {
					SelectOptionVo selectOption = tuple.getT1();
					String apartmentCode = tuple.getT2().apartmentCode();
					if (!selectOption.agenda().getApartmentCode().equals(apartmentCode)) {
						return Mono.error(new VoteException(VoteExceptionCode.NO_RIGHT_FOR_VOTE));
					} else {
						return Mono.just(selectOption);
					}
				})
				.switchIfEmpty(Mono.defer(() -> Mono.error(new VoteException(VoteExceptionCode.SELECT_OPTION_NOT_FOUND))))
				.map(selectOption -> {
					VoteEvent voteEvent= new VoteEvent(selectOption.id(), voteCreationDto.userId());
					kafkaProducer.send(voteEvent);
					return new VoteCreationResultDto(selectOption.agenda().getTitle(), selectOption.summary());
				});
		}

	@Override
	@Transactional(readOnly = true)
	public Mono<VoteHistory> getVoteHistory(Long agendaId) {

		return Mono.deferContextual(contextView -> {
			UserDto userDto = contextView.get("user");
			return voteRepository.findVoteHistoryByUserIdAndAgendaId(userDto.id(), agendaId);
		}).switchIfEmpty(Mono.error(() -> new VoteException(VoteExceptionCode.VOTE_HISTORY_NOT_FOUND)));
	}
}
