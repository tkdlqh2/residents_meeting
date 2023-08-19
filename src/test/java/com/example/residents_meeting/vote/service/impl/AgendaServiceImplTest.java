package com.example.residents_meeting.vote.service.impl;

import com.example.residents_meeting.vote.domain.Agenda;
import com.example.residents_meeting.vote.domain.AgendaHistory;
import com.example.residents_meeting.vote.domain.SelectOption;
import com.example.residents_meeting.vote.domain.SelectOptionHistory;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationDTO;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationResultDTO;
import com.example.residents_meeting.vote.domain.dto.SelectOptionCreationDto;
import com.example.residents_meeting.vote.exception.VoteException;
import com.example.residents_meeting.vote.exception.VoteExceptionCode;
import com.example.residents_meeting.vote.repository.AgendaHistoryRepository;
import com.example.residents_meeting.vote.repository.AgendaRepository;
import com.example.residents_meeting.vote.repository.SelectOptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AgendaServiceImplTest {
	@Mock
	private AgendaRepository mockAgendaRepository;
	@Mock
	private SelectOptionRepository mockSelectOptionRepository;

	@Mock
	private AgendaHistoryRepository mockAgendaHistoryRepository;

	@InjectMocks
	private AgendaServiceImpl agendaServiceImplUnderTest;

	@Test
	void createAgenda() {
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

		//when
		AgendaCreationResultDTO result = agendaServiceImplUnderTest.createAgenda(creationDTO);

		//then
		verify(mockAgendaRepository,times(1)).save(any(Agenda.class));
		
		assertEquals("A12345678", result.apartmentCode());
		assertEquals("제목", result.title());
		assertEquals("설명", result.details());
		assertEquals(LocalDate.now().plusDays(3), result.endDate());
		assertEquals(2, result.selectOptionSummaryList().size());
		assertTrue(result.selectOptionSummaryList().containsAll(List.of("찬성", "반대")));
	}

	@Test
	@DisplayName("안건 이력 가져오기 성공 - AgendaHistoryRepository 에서 가져온다")
	void getAgendaHistorySuccess_FromAgendaHistoryRepo() {
		//given
		Long agendaId = 1L;
		String title = "안건 제목";
		String details = "안건 설명";
		LocalDate endDate = LocalDate.now().minusDays(3);

		given(mockAgendaRepository.findEndDateById(agendaId))
				.willReturn(Optional.of(endDate));

		given(mockAgendaHistoryRepository.findById(agendaId))
				.willReturn(Optional.of(
						AgendaHistory.builder()
								.title(title)
								.details(details)
								.endDate(LocalDate.now().minusDays(3))
								.selectOptions(
										List.of(
												new SelectOptionHistory("찬성", "찬성입니다.", 1),
												new SelectOptionHistory("반대", "반대입니다.", 2)
										)
								)
								.build()
				));

		//when
		AgendaHistory agendaHistory = agendaServiceImplUnderTest.getAgendaHistory(agendaId);

		//then
		verify(mockAgendaRepository, times(1)).findEndDateById(agendaId);
		verify(mockAgendaHistoryRepository, times(1)).findById(agendaId);
		assertEquals(title, agendaHistory.getTitle());
		assertEquals(details, agendaHistory.getDetails());
		assertEquals(endDate, agendaHistory.getEndDate());

		var selectOptionHistoryList = agendaHistory.getSelectOptions();
		assertEquals(2, selectOptionHistoryList.size());
		assertEquals("찬성", selectOptionHistoryList.get(0).getSummary());
		assertEquals("찬성입니다.", selectOptionHistoryList.get(0).getDetails());
		assertEquals(1, selectOptionHistoryList.get(0).getCount());
	}

	@Test
	@DisplayName("안건 이력 가져오기 성공 - AgendaRepository 에서 가져온다")
	void getAgendaHistorySuccess_FromAgendaRepo() {
		//given
		Long agendaId = 1L;
		String title = "안건 제목";
		String details = "안건 설명";
		LocalDate endDate = LocalDate.now().minusDays(3);

		given(mockAgendaRepository.findEndDateById(agendaId))
				.willReturn(Optional.of(endDate));

		given(mockAgendaHistoryRepository.findById(agendaId))
				.willReturn(Optional.empty());

		Agenda mockAgenda = new TestAgenda(
				agendaId,
				"A12345678",
				title,
				details,
				endDate);

		mockAgenda.setSelectOptions(
				List.of(
						new TestSelectOption(1L, mockAgenda, "찬성","찬성입니다."),
						new TestSelectOption(2L, mockAgenda, "반대", "반대입니다.")
				)
		);

		given(mockAgendaRepository.findByIdUsingFetchJoin(agendaId))
				.willReturn(Optional.of(mockAgenda));

		given(mockSelectOptionRepository.countById(1L))
				.willReturn(1);

		given(mockSelectOptionRepository.countById(2L))
				.willReturn(2);

		//when
		AgendaHistory agendaHistory = agendaServiceImplUnderTest.getAgendaHistory(agendaId);

		//then
		verify(mockAgendaRepository, times(1)).findEndDateById(agendaId);
		verify(mockAgendaHistoryRepository, times(1)).findById(agendaId);
		assertEquals(title, agendaHistory.getTitle());
		assertEquals(details, agendaHistory.getDetails());
		assertEquals(endDate, agendaHistory.getEndDate());

		var selectOptionHistoryList = agendaHistory.getSelectOptions();
		assertEquals(2, selectOptionHistoryList.size());
		assertEquals("찬성", selectOptionHistoryList.get(0).getSummary());
		assertEquals("찬성입니다.", selectOptionHistoryList.get(0).getDetails());
		assertEquals(1, selectOptionHistoryList.get(0).getCount());
		assertEquals(2, selectOptionHistoryList.get(1).getCount());
	}

	@Test
	@DisplayName("안건 이력 가져오기 실패 - 안건을 찾을 수 없음")
	void getAgendaHistoryFail_FromAgendaRepo() {
		//given
		Long agendaId = 1L;

		given(mockAgendaRepository.findEndDateById(anyLong()))
				.willReturn(Optional.empty());

		//when & then
		try {
			agendaServiceImplUnderTest.getAgendaHistory(agendaId);
			throw new RuntimeException("테스트 실패");
		} catch (Exception e) {
			assertTrue(e instanceof VoteException);
			assertEquals(VoteExceptionCode.AGENDA_NOT_FOUND.getMessage(), e.getMessage());
		}
	}

	@Test
	@DisplayName("안건 이력 가져오기 실패 - 투표가 아직 진행중임")
	void getAgendaHistoryFail_OngoingVote() {
		//given
		Long agendaId = 1L;

		given(mockAgendaRepository.findEndDateById(anyLong()))
				.willReturn(Optional.of(LocalDate.now().plusDays(1)));

		//when & then
		try {
			agendaServiceImplUnderTest.getAgendaHistory(agendaId);
			throw new RuntimeException("테스트 실패");
		} catch (Exception e) {
			assertTrue(e instanceof VoteException);
			assertEquals(VoteExceptionCode.ONGOING_SECRET_VOTE.getMessage(), e.getMessage());
		}
	}

	@Test
	@DisplayName("선택지에 투표한 유저 아이디 목록 가져오기 성공")
	void getListOfUserIdOfSelectOptionId() {
		//given
		Long agendaId = 2L;
		Long selectOptionId = 1L;
		List<Long> userIdList = List.of(1L, 2L, 3L);

		given(mockAgendaRepository.findEndDateById(anyLong()))
				.willReturn(Optional.of(LocalDate.now().minusDays(1)));

		given(mockSelectOptionRepository.findUserIdsByAgendaIdAndId(agendaId, selectOptionId))
				.willReturn(userIdList);

		//when
		List<Long> result = agendaServiceImplUnderTest.getListOfUserIdOfAgendaAndSelectOption(agendaId, selectOptionId);

		//then

		verify(mockAgendaRepository,times(1)).findEndDateById(agendaId);
		verify(mockSelectOptionRepository, times(1)).findUserIdsByAgendaIdAndId(agendaId, selectOptionId);
		assertEquals(userIdList, result);

	}

	static class TestAgenda extends Agenda {
		public TestAgenda(Long id, String apartmentCode, String title, String details, LocalDate endDate) {
			super(id, apartmentCode, title, details, endDate);
		}
	}

	static class TestSelectOption extends SelectOption {
		public TestSelectOption(Long id, Agenda agenda,  String summary, String details) {
			super(id, agenda, summary, details);
		}
	}
}