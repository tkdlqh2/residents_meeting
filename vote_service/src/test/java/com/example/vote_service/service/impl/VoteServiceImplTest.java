package com.example.vote_service.service.impl;

import com.example.vote_service.UserInfo;
import com.example.vote_service.domain.dto.SelectOptionVo;
import com.example.vote_service.domain.dto.VoteCreationDto;
import com.example.vote_service.domain.dto.VoteEvent;
import com.example.vote_service.domain.dto.VoteHistory;
import com.example.vote_service.exception.VoteException;
import com.example.vote_service.exception.VoteExceptionCode;
import com.example.vote_service.messagequeue.KafkaProducer;
import com.example.vote_service.messagequeue.MessageProduceResult;
import com.example.vote_service.repository.agenda.AgendaCustomRepository;
import com.example.vote_service.repository.select_option.SelectOptionCustomRepository;
import com.example.vote_service.repository.vote.VoteCustomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class VoteServiceImplTest {
	@Mock
	private AgendaCustomRepository agendaCustomRepository;
	@Mock
	private SelectOptionCustomRepository selectOptionRepository;
	@Mock
	private VoteCustomRepository voteRepository;
	@Mock
	private KafkaProducer kafkaProducer;
	@InjectMocks
	private VoteServiceImpl voteService;

	@Test
	@DisplayName("투표 생성 성공 - KafkaProducer 에서 성공하면 true 를 반환한다")
	void createVoteSuccess() {
		//given

		Long selectOptionId = 1L;
		Long userId = 2L;
		Long agendaId = 3L;
		String ApartmentCode = "A12345678";

		VoteCreationDto voteCreationDto = new VoteCreationDto(selectOptionId);

		given(selectOptionRepository.findById(selectOptionId)).willReturn(
				Mono.just(new SelectOptionVo(selectOptionId, agendaId, "찬성", "찬성입니다.",null,null)));

		given(agendaCustomRepository.findApartmentCodeById(agendaId))
				.willReturn(Mono.just(ApartmentCode));

		given(kafkaProducer.send(any()))
				.willReturn(Mono.just(new MessageProduceResult(VoteEvent.toEvent(voteCreationDto, userId))));

		UserInfo userInfo =  new UserInfo(userId,
				null,
				null,
				null,
				new UserInfo.Address(ApartmentCode,0,0),
				null);

		//when & then
		StepVerifier.create(voteService.createVote(voteCreationDto).contextWrite(
						context -> context.put("user",userInfo)
				))
				.expectNext(true)
				.verifyComplete();
	}

	@Test
	@DisplayName("투표 생성 실패 - KafkaProducer 에서 실패하면 false 를 반환한다")
	void createVoteFail_FromKafkaProducer() {
		//given

		Long selectOptionId = 1L;
		Long userId = 2L;
		Long agendaId = 3L;
		String ApartmentCode = "A12345678";

		VoteCreationDto voteCreationDto = new VoteCreationDto(selectOptionId);

		given(selectOptionRepository.findById(selectOptionId)).willReturn(
				Mono.just(new SelectOptionVo(selectOptionId, agendaId, "찬성", "찬성입니다.",null,null)));

		given(agendaCustomRepository.findApartmentCodeById(agendaId))
				.willReturn(Mono.just(ApartmentCode));

		given(kafkaProducer.send(any()))
				.willReturn(Mono.just(new MessageProduceResult(VoteEvent.toEvent(voteCreationDto, userId),
						new RuntimeException())));

		UserInfo userInfo =  new UserInfo(userId,
				null,
				null,
				null,
				new UserInfo.Address(ApartmentCode,0,0),
				null);


		//when & then
		StepVerifier.create(voteService.createVote(voteCreationDto).contextWrite(
						context -> context.put("user", userInfo
						)))
				.expectNext(false)
				.verifyComplete();
	}

	@Test
	@DisplayName("투표 생성 실패 - 선택지를 찾을 수 없음")
	void createVoteFail_SelectOptionNotFound() {
		//given
		Long selectOptionId = 1L;
		Long userId = 2L;
		String ApartmentCode = "A12345678";

		VoteCreationDto voteCreationDto = new VoteCreationDto(selectOptionId);

		given(selectOptionRepository.findById(selectOptionId)).willReturn(
				Mono.empty());

		UserInfo userInfo =  new UserInfo(userId,
				null,
				null,
				null,
				new UserInfo.Address(ApartmentCode,0,0),
				null);

		//when & then
		StepVerifier.create(voteService.createVote(voteCreationDto).contextWrite(
						context -> context.put("user", userInfo
						)))
				.expectErrorMatches(throwable ->
						throwable instanceof VoteException &&
						throwable.getMessage().equals(VoteExceptionCode.SELECT_OPTION_NOT_FOUND.getMessage()))
				.verify();
	}

	@Test
	@DisplayName("투표 생성 실패 - 해당 아파트 주민이 아님")
	void createVoteFail_NoRightForVote() {
		//given
		Long selectOptionId = 1L;
		Long userId = 2L;
		Long agendaId = 3L;
		String ApartmentCode = "A12345678";

		VoteCreationDto voteCreationDto = new VoteCreationDto(selectOptionId);

		given(selectOptionRepository.findById(selectOptionId)).willReturn(
				Mono.just(new SelectOptionVo(selectOptionId, agendaId, "찬성", "찬성입니다.",null,null)));

		given(agendaCustomRepository.findApartmentCodeById(agendaId))
				.willReturn(Mono.just("A12345679"));

		UserInfo userInfo =  new UserInfo(userId,
				null,
				null,
				null,
				new UserInfo.Address(ApartmentCode,0,0),
				null);

		//when & then
		StepVerifier.create(voteService.createVote(voteCreationDto).contextWrite(
						context -> context.put("user", userInfo
						)))
				.expectErrorMatches(throwable ->
						throwable instanceof VoteException &&
								throwable.getMessage().equals(VoteExceptionCode.NO_RIGHT_FOR.getMessage()))
				.verify();
	}

	@Test
	@DisplayName("투표 이력 가져오기 성공")
	void getVoteHistorySuccess() {
		//given
		Long selectOptionId = 1L;
		Long userId = 2L;
		Long agendaId = 3L;
		String ApartmentCode = "A12345678";
		VoteHistory voteHistory =
				new VoteHistory(selectOptionId,
						LocalDateTime.of(2021, 1, 1, 1, 1, 1));

		given(voteRepository.findVoteHistoryByUserIdAndAgendaId(userId,agendaId)).willReturn(
				Mono.just(voteHistory));

		UserInfo userInfo =  new UserInfo(userId,
				null,
				null,
				null,
				new UserInfo.Address(ApartmentCode,0,0),
				null);

		//when & then
		StepVerifier.create(voteService.getVoteHistory(agendaId).contextWrite(
						context -> context.put("user", userInfo
						)))
				.expectNext(voteHistory)
				.verifyComplete();

	}

	@Test
	@DisplayName("투표 이력 가져오기 성공_이력이 없음")
	void getVoteHistorySuccess_EmptyHistory() {
		//given
		Long userId = 2L;
		Long agendaId = 3L;
		String ApartmentCode = "A12345678";

		given(voteRepository.findVoteHistoryByUserIdAndAgendaId(userId,agendaId)).willReturn(
				Mono.empty());

		UserInfo userInfo =  new UserInfo(userId,
				null,
				null,
				null,
				new UserInfo.Address(ApartmentCode,0,0),
				null);

		//when & then
		StepVerifier.create(voteService.getVoteHistory(agendaId).contextWrite(
						context -> context.put("user", userInfo
						)))
				.expectNextMatches(voteHistory ->
						voteHistory.selectOptionId() == null &&
						voteHistory.voteTime() == null)
				.verifyComplete();

	}
}