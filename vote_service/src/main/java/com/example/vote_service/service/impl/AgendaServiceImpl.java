package com.example.vote_service.service.impl;

import com.example.vote_service.domain.Agenda;
import com.example.vote_service.domain.AgendaHistory;
import com.example.vote_service.domain.SelectOption;
import com.example.vote_service.domain.SelectOptionHistory;
import com.example.vote_service.domain.dto.AgendaCreationDTO;
import com.example.vote_service.domain.dto.AgendaCreationResultDTO;
import com.example.vote_service.exception.VoteException;
import com.example.vote_service.exception.VoteExceptionCode;
import com.example.vote_service.repository.agenda.AgendaCustomRepository;
import com.example.vote_service.repository.agenda.AgendaHistoryRepository;
import com.example.vote_service.repository.agenda.AgendaRepository;
import com.example.vote_service.repository.select_option.SelectOptionRepository;
import com.example.vote_service.service.AgendaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
public class AgendaServiceImpl implements AgendaService {

	private final AgendaRepository agendaRepository;
	private final AgendaCustomRepository agendaCustomRepository;
	private final SelectOptionRepository selectOptionRepository;
	private final AgendaHistoryRepository agendaHistoryRepository;

	public AgendaServiceImpl(AgendaRepository agendaRepository,
							 AgendaCustomRepository agendaCustomRepository,
							 SelectOptionRepository selectOptionRepository,
							 AgendaHistoryRepository agendaHistoryRepository) {
		this.agendaRepository = agendaRepository;
		this.agendaCustomRepository = agendaCustomRepository;
		this.selectOptionRepository = selectOptionRepository;
		this.agendaHistoryRepository = agendaHistoryRepository;
	}

	@Override
	@Transactional
	public Mono<AgendaCreationResultDTO> createAgenda(AgendaCreationDTO creationDTO) {
		return Mono.just(creationDTO)
				.map(Agenda::from)
				.flatMap(agendaRepository::save)
				.log()
				.zipWith(Mono.just(creationDTO.selectOptionCreationDtoList()))
				.flatMap(tuple -> {
						Agenda savedAgenda = tuple.getT1();
						System.out.println(savedAgenda.getId());
						List<SelectOption> selectOptions = tuple.getT2()
								.stream()
								.map(selectOptionCreationDto -> SelectOption.from(savedAgenda, selectOptionCreationDto))
								.toList();

						return selectOptionRepository.saveAll(selectOptions)
								.collectList()
								.map(savedSelectOptions -> AgendaCreationResultDTO.from(savedAgenda, savedSelectOptions));
				});
	}

	@Transactional(readOnly = true)
	@Override
	public Flux<AgendaHistory> getAgendaHistory(Long agendaId) {
		return Flux.interval(Duration.ofSeconds(2))
				.flatMap(this::checkOngoingSecretVote)
				.flatMap(time -> agendaHistoryRepository.findById(agendaId))
				.switchIfEmpty(Mono.defer(() -> {
					 return agendaCustomRepository.findByIdUsingFetchJoin(agendaId)
							.switchIfEmpty(Mono.error(() -> new VoteException(VoteExceptionCode.AGENDA_NOT_FOUND)))
							.flatMap(agenda -> {
								Flux<SelectOptionHistory> selectOptionHistoryFlux = selectOptionRepository.findAllByAgendaId(agendaId)
										.flatMap(selectOption -> {
											Mono<Integer> selectOptionCount = selectOptionRepository.countById(selectOption.getId())
													.defaultIfEmpty(0);
											return selectOptionCount.map(count -> new SelectOptionHistory(
													selectOption.getSummary(),
													selectOption.getDetails(),
													count
											));
										});
								return selectOptionHistoryFlux.collectList()
										.map(selectOptionHistories -> AgendaHistory.builder()
												.title(agenda.getTitle())
												.details(agenda.getDetails())
												.endDate(agenda.getEndDate())
												.selectOptions(selectOptionHistories)
												.build());
							});
				}));

	}

	@Transactional(readOnly = true)
	@Override
	public Flux<List<Long>> getListOfUserIdOfAgendaAndSelectOption(Long agendaId, Long selectOptionId) {
		return Flux.interval(Duration.ofSeconds(2))
				.flatMap(this::checkOngoingSecretVote)
				.flatMap(time -> checkOngoingSecretVote(agendaId))
				.flatMap(x -> selectOptionRepository.findUserIdsByAgendaIdAndId(agendaId, selectOptionId));
	}


	private Mono<Void> checkOngoingSecretVote(Long agendaId) {

		return agendaRepository.findEndDateById(agendaId)
				.switchIfEmpty(Mono.error(() -> new VoteException(VoteExceptionCode.AGENDA_NOT_FOUND)))
				.flatMap(
					date -> {
						if (LocalDate.now().isBefore(date)) {
							return Mono.error(() -> new VoteException(VoteExceptionCode.ONGOING_SECRET_VOTE));
						}
						return Mono.empty();
				}
		);
	}
}
