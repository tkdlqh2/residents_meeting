package com.example.residents_meeting.vote.service.impl;

import com.example.residents_meeting.vote.domain.Agenda;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationDTO;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationResultDTO;
import com.example.residents_meeting.vote.domain.dto.SelectOptionCreationDto;
import com.example.residents_meeting.vote.repository.AgendaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AgendaServiceImplTest {
	@Mock
	private AgendaRepository mockAgendaRepository;

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
}