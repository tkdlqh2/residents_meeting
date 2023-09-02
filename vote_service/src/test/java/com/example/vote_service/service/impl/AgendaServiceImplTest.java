package com.example.vote_service.service.impl;

import com.example.vote_service.domain.AgendaHistory;
import com.example.vote_service.domain.SelectOptionHistory;
import com.example.vote_service.domain.dto.*;
import com.example.vote_service.exception.VoteException;
import com.example.vote_service.exception.VoteExceptionCode;
import com.example.vote_service.messagequeue.KafkaProducer;
import com.example.vote_service.messagequeue.MessageProduceResult;
import com.example.vote_service.repository.agenda.AgendaCustomRepository;
import com.example.vote_service.repository.agenda.AgendaHistoryRepository;
import com.example.vote_service.repository.select_option.SelectOptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AgendaServiceImplTest {
	@Mock
	private KafkaProducer kafkaProducer;
	@Mock
	private AgendaCustomRepository agendaCustomRepository;
	@Mock
	private SelectOptionRepository selectOptionRepository;
	@Mock
	private AgendaHistoryRepository agendaHistoryRepository;
	@InjectMocks
	private AgendaServiceImpl agendaService;

	@Test
	@DisplayName("안건 생성 성공 - KafkaProducer 에서 성공하면 true 를 반환한다")
	void createAgendaSuccess() {
		//given
		List<SelectOptionCreationDto> selectOptionCreationDtoList = List.of(
				new SelectOptionCreationDto("찬성", "찬성입니다."),
				new SelectOptionCreationDto("반대", "반대입니다.")
		);
		AgendaCreationDTO creationDTO = new AgendaCreationDTO(
				"A12345678",
				"제목",
				"설명",
				LocalDate.now().plusDays(3),
				selectOptionCreationDtoList
		);

		given(kafkaProducer.send(any(AgendaEvent.class)))
				.willReturn(Mono.just(new MessageProduceResult(AgendaEvent.from(creationDTO))));

		//when & then
		StepVerifier.create(agendaService.createAgenda(creationDTO))
				.expectNext(true)
				.verifyComplete();

	}

	@Test
	@DisplayName("안건 생성 성공 - KafkaProducer 에서 실패하면 false 를 반환한다")
	void createAgendaFail() {
		//given
		List<SelectOptionCreationDto> selectOptionCreationDtoList = List.of(
				new SelectOptionCreationDto("찬성", "찬성입니다."),
				new SelectOptionCreationDto("반대", "반대입니다.")
		);
		AgendaCreationDTO creationDTO = new AgendaCreationDTO(
				"A12345678",
				"제목",
				"설명",
				LocalDate.now().plusDays(3),
				selectOptionCreationDtoList
		);

		given(kafkaProducer.send(any(AgendaEvent.class)))
				.willReturn(Mono.just(new MessageProduceResult(AgendaEvent.from(creationDTO), new RuntimeException())));

		//when & then
		StepVerifier.create(agendaService.createAgenda(creationDTO))
				.expectNext(false)
				.verifyComplete();
	}

	@Test
	@DisplayName("안건 이력 가져오기 성공 - AgendaHistoryRepository 에서 가져온다")
	void getAgendaHistorySuccess_FromAgendaHistoryRepo() {
		//given
		Long agendaId = 1L;
		String title = "안건 제목";
		String details = "안건 설명";
		LocalDate endDate = LocalDate.now().minusDays(3);

		AgendaHistory agendaHistory = AgendaHistory.builder()
				.title(title)
				.details(details)
				.endDate(LocalDate.now().minusDays(3))
				.selectOptions(
						List.of(
								new SelectOptionHistory("찬성", "찬성입니다.", 1),
								new SelectOptionHistory("반대", "반대입니다.", 2)
						)
				)
				.build();

		given(agendaCustomRepository.findEndDateById(agendaId))
				.willReturn(Mono.just(endDate));

		given(agendaHistoryRepository.findById(agendaId))
				.willReturn(Mono.just(agendaHistory));

		StepVerifier
				.withVirtualTime(() -> agendaService.getAgendaHistory(agendaId))
				.expectSubscription()
				.then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(10*5)))
				.expectNextCount(5)
				.expectNext(agendaHistory)
				.thenCancel()
				.verify();
	}

	@Test
	@DisplayName("안건 이력 가져오기 성공 - Agenda,SelectOption 에서 가져온다")
	void getAgendaHistorySuccess_FromAgendaRepo() {
		//given
		Long agendaId = 1L;
		String title = "안건 제목";
		String details = "안건 설명";
		LocalDate endDate = LocalDate.now().minusDays(3);

		given(agendaCustomRepository.findEndDateById(agendaId))
				.willReturn(Mono.just(endDate));

		given(agendaHistoryRepository.findById(agendaId))
				.willReturn(Mono.empty());

		given(agendaCustomRepository.findByIdUsingFetchJoin(agendaId))
				.willReturn(Mono.just(AgendaVo.builder()
								.id(agendaId)
								.title(title)
								.details(details)
								.selectOptionList(
										List.of(
												new SelectOptionVo(1L,agendaId,"찬성", "찬성입니다.", null,null),
												new SelectOptionVo(2L,agendaId,"반대", "반대입니다.", null,null)
										)
								)
						.build()));

		given(selectOptionRepository.countById(1L))
				.willReturn(Mono.just(1));
		given(selectOptionRepository.countById(2L))
				.willReturn(Mono.just(2));

		StepVerifier
				.withVirtualTime(() -> agendaService.getAgendaHistory(agendaId))
				.expectSubscription()
				.then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(10*5)))
				.expectNextCount(5)
				.thenCancel()
				.verify();
	}

	@Test
	@DisplayName("안건 이력 가져오기 실패 - AgendaNotFound")
	void getAgendaHistoryFail_AgendaNotFound() {
		//given
		Long agendaId = 1L;

		given(agendaCustomRepository.findEndDateById(agendaId))
				.willReturn(Mono.empty());

		StepVerifier
				.create(agendaService.getAgendaHistory(agendaId))
				.expectSubscription()
				.expectErrorMatches(throwable ->
						throwable instanceof VoteException &&
						throwable.getMessage().equals(VoteExceptionCode.AGENDA_NOT_FOUND.getMessage()))
				.verify();
	}

	@Test
	@DisplayName("안건 이력 가져오기 실패 - Ongoing Secret Vote")
	void getAgendaHistoryFail_OngoingSecretVote() {
		//given
		Long agendaId = 1L;
		LocalDate endDate = LocalDate.now().plusDays(3);

		given(agendaCustomRepository.findEndDateById(agendaId))
				.willReturn(Mono.just(endDate));

		StepVerifier
				.create(agendaService.getAgendaHistory(agendaId))
				.expectSubscription()
				.expectErrorMatches(throwable ->
						throwable instanceof VoteException &&
						throwable.getMessage().equals(VoteExceptionCode.ONGOING_SECRET_VOTE.getMessage()))
				.verify();
	}


	@Test
	void getListOfUserIdOfAgendaAndSelectOption() {
		//given
		Long agendaId = 1L;
		Long selectOptionId = 2L;
		LocalDate endDate = LocalDate.now().minusDays(3);
		List<Long> userIds = List.of(1L, 2L, 3L);

		given(agendaCustomRepository.findEndDateById(agendaId))
				.willReturn(Mono.just(endDate));

		given(selectOptionRepository.findUserIdsByAgendaIdAndId(agendaId, selectOptionId))
				.willReturn(Mono.just(userIds));


		//when & then
		StepVerifier
				.withVirtualTime(() -> agendaService.getListOfUserIdOfAgendaAndSelectOption(agendaId, selectOptionId))
				.expectSubscription()
				.then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(10*5)))
				.expectNextCount(5)
				.expectNext(userIds)
				.thenCancel()
				.verify();

	}
}