package com.example.vote_service.service.impl;

import com.example.vote_service.domain.AgendaHistory;
import com.example.vote_service.domain.SelectOptionHistory;
import com.example.vote_service.domain.dto.AgendaCreationDTO;
import com.example.vote_service.domain.dto.AgendaEvent;
import com.example.vote_service.exception.VoteException;
import com.example.vote_service.exception.VoteExceptionCode;
import com.example.vote_service.messagequeue.KafkaProducer;
import com.example.vote_service.messagequeue.MessageProduceResult;
import com.example.vote_service.repository.agenda.AgendaCustomRepository;
import com.example.vote_service.repository.agenda.AgendaHistoryRepository;
import com.example.vote_service.repository.select_option.SelectOptionRepository;
import com.example.vote_service.service.AgendaService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class AgendaServiceImpl implements AgendaService {

	private final KafkaProducer kafkaProducer;
	private final AgendaCustomRepository agendaCustomRepository;
	private final SelectOptionRepository selectOptionRepository;
	private final AgendaHistoryRepository agendaHistoryRepository;

	public AgendaServiceImpl(KafkaProducer kafkaProducer,
							 AgendaCustomRepository agendaCustomRepository,
							 SelectOptionRepository selectOptionRepository,
							 AgendaHistoryRepository agendaHistoryRepository) {
		this.kafkaProducer = kafkaProducer;
		this.agendaCustomRepository = agendaCustomRepository;
		this.selectOptionRepository = selectOptionRepository;
		this.agendaHistoryRepository = agendaHistoryRepository;
	}

	@Override
	public Mono<Boolean> createAgenda(AgendaCreationDTO creationDTO) {
		return Mono.just(creationDTO)
				.mapNotNull(AgendaEvent::from)
				.flatMap(kafkaProducer::send)
				.map(MessageProduceResult::getStatus);
	}

	@Override
	public Flux<AgendaHistory> getAgendaHistory(Long agendaId) {
		return checkOngoingSecretVote(agendaId).flux()
				.flatMap(date -> Flux.interval(Duration.ofSeconds(2)))
				.flatMap(time -> agendaHistoryRepository.findById(agendaId)
						.switchIfEmpty(Mono.defer(() -> getAgendaHistoryMonoFromRepo(agendaId))));

	}

	private Mono<AgendaHistory> getAgendaHistoryMonoFromRepo(Long agendaId) {
		return agendaCustomRepository.findByIdUsingFetchJoin(agendaId)
				.switchIfEmpty(Mono.error(() -> new VoteException(VoteExceptionCode.AGENDA_NOT_FOUND)))
				.flatMap(agendaVo -> {
					Flux<SelectOptionHistory> selectOptionHistoryFlux =
							Flux.fromIterable(agendaVo.selectOptionList()).flatMap(
									selectOption -> {
										Mono<Integer> selectOptionCount = selectOptionRepository.countById(selectOption.id())
												.defaultIfEmpty(0);
										return selectOptionCount.map(count -> new SelectOptionHistory(
												selectOption.summary(),
												selectOption.details(),
												count
										));
									});

					return selectOptionHistoryFlux.collectList()
							.map(selectOptionHistories -> AgendaHistory.builder()
									.title(agendaVo.title())
									.details(agendaVo.details())
									.endDate(agendaVo.endDate())
									.selectOptions(selectOptionHistories)
									.build());
				});
	}

	@Override
	public Flux<List<Long>> getListOfUserIdOfAgendaAndSelectOption(Long agendaId, Long selectOptionId) {
		return checkOngoingSecretVote(agendaId)
				.flux()
				.flatMap(date -> Flux.interval(Duration.ofSeconds(2)))
				.flatMap(time ->
						Mono.defer(() ->
								selectOptionRepository.findUserIdsByAgendaIdAndId(agendaId, selectOptionId)
										.collectList()
										.switchIfEmpty(Mono.just(Collections.emptyList()))
						));
	}

	private Mono<LocalDate> checkOngoingSecretVote(Long agendaId) {

		return agendaCustomRepository.findEndDateById(agendaId)
				.switchIfEmpty(Mono.error(() -> new VoteException(VoteExceptionCode.AGENDA_NOT_FOUND)))
				.flatMap(
					date -> {
						if (LocalDate.now().isBefore(date)) {
							return Mono.error(() -> new VoteException(VoteExceptionCode.ONGOING_SECRET_VOTE));
						}
						return Mono.just(date);
				}
		);
	}
}
