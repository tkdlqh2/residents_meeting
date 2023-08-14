package com.example.residents_meeting.vote.service.impl;

import com.example.residents_meeting.common.RequestContextHolder;
import com.example.residents_meeting.user.domain.Address;
import com.example.residents_meeting.user.domain.User;
import com.example.residents_meeting.user.domain.UserRole;
import com.example.residents_meeting.vote.domain.Agenda;
import com.example.residents_meeting.vote.domain.SelectOption;
import com.example.residents_meeting.vote.domain.dto.VoteCreationDto;
import com.example.residents_meeting.vote.domain.dto.VoteCreationResultDto;
import com.example.residents_meeting.vote.domain.dto.VoteHistory;
import com.example.residents_meeting.vote.exception.VoteException;
import com.example.residents_meeting.vote.exception.VoteExceptionCode;
import com.example.residents_meeting.vote.repository.SelectOptionRepository;
import com.example.residents_meeting.vote.repository.VoteRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class VoteServiceImplTest {
	private static final Long USER_ID = 1L;
	private static final Long AGENDA_ID = 2L;
	private static final Long SELECT_OPTION_ID = 3L;

	private static TestUser DEFAULT_USER;
	@Mock
	private RequestContextHolder requestContextHolder;
	@Mock
	private SelectOptionRepository selectOptionRepository;
	@Mock
	private VoteRepository voteRepository;
	@InjectMocks
	private VoteServiceImpl voteServiceImpl;


	@BeforeAll
	static void setUp() {
		DEFAULT_USER = new TestUser("test",
				"test",
				"test",
				"test",
				new Address("A12345678", 101, 101),
				UserRole.LEADER);
		DEFAULT_USER.setId(USER_ID);
	}

	@Test
	@DisplayName("투표 생성 성공")
	void createVoteTestSuccess() {
		//given
		given(requestContextHolder.getUser()).willReturn(DEFAULT_USER);

		TestAgenda defaultAgenda =  new TestAgenda(AGENDA_ID, "A12345678","안건 제목", "설명",LocalDate.now().plusDays(3));

		TestSelectOption defaultSelectOption = new TestSelectOption(SELECT_OPTION_ID, defaultAgenda, "선택지 설명", null);

		given(selectOptionRepository.findByAgendaIdAndId(AGENDA_ID, SELECT_OPTION_ID))
				.willReturn(Optional.of(defaultSelectOption));

		VoteCreationDto voteCreationDto = new VoteCreationDto(AGENDA_ID, SELECT_OPTION_ID);

		//when
		VoteCreationResultDto resultDto = voteServiceImpl.createVote(voteCreationDto);

		//then
		assertEquals("안건 제목", resultDto.agendaTitle());
		assertEquals("선택지 설명", resultDto.selectOptionSummary());
	}

	@Test
	@DisplayName("투표 생성 실패 - 선택지가 없는 경우")
	void createVoteTestFail_NoSelectOption() {
		//given
		VoteCreationDto voteCreationDto = new VoteCreationDto(AGENDA_ID, SELECT_OPTION_ID);
		given(selectOptionRepository.findByAgendaIdAndId(AGENDA_ID, SELECT_OPTION_ID))
				.willReturn(Optional.empty());

		//when & then
		try{
			voteServiceImpl.createVote(voteCreationDto);
			throw new RuntimeException("Test Fail");
		} catch (Exception e) {
			assertTrue(e instanceof VoteException);
			assertEquals(VoteExceptionCode.SELECT_OPTION_NOT_FOUND.getMessage(), e.getMessage());
		}
	}

	@Test
	@DisplayName("투표 생성 실패 - 해당 아파트 주민이 아닌 경우")
	void createVoteTestFail_NoRightForVote() {
		//given
		given(requestContextHolder.getUser()).willReturn(DEFAULT_USER);

		TestAgenda defaultAgenda =  new TestAgenda(AGENDA_ID, "87654321","안건 제목", "설명",LocalDate.now().plusDays(3));

		TestSelectOption defaultSelectOption = new TestSelectOption(SELECT_OPTION_ID, defaultAgenda, "선택지 설명", null);

		given(selectOptionRepository.findByAgendaIdAndId(AGENDA_ID, SELECT_OPTION_ID))
				.willReturn(Optional.of(defaultSelectOption));

		VoteCreationDto voteCreationDto = new VoteCreationDto(AGENDA_ID, SELECT_OPTION_ID);

		//when & then
		try{
			voteServiceImpl.createVote(voteCreationDto);
			throw new RuntimeException("Test Fail");
		} catch (Exception e) {
			assertTrue(e instanceof VoteException);
			assertEquals(VoteExceptionCode.NO_RIGHT_FOR_VOTE.getMessage(), e.getMessage());
		}
	}

	@Test
	@DisplayName("안건에 대한 가장 최근 투표 출력 성공")
	void getVoteHistory() {
		//given
		Long agendaId = 3L;
		LocalDateTime givenDateTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
		given(requestContextHolder.getUser()).willReturn(DEFAULT_USER);
		given(voteRepository.findVoteHistoryByUserIdAndAgendaId(USER_ID,agendaId))
				.willReturn(new VoteHistory(3L, givenDateTime));

		//when
		VoteHistory voteHistory1 = voteServiceImpl.getVoteHistory(agendaId);
		VoteHistory voteHistory2 = voteServiceImpl.getVoteHistory(agendaId+1);
		//then
		assertEquals(3L, voteHistory1.selectOptionId());
		assertEquals(givenDateTime, voteHistory1.voteTime());
		assertNull(voteHistory2);
	}

	static class TestUser extends User {
		public TestUser(String username, String password, String name, String phone, Address address, UserRole role) {
			super(username, password, name, phone, address, role);
		}

		public void setId(Long id) {
			super.setId(id);
		}
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