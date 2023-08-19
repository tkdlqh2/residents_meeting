package com.example.residents_meeting.vote.controller;

import com.example.residents_meeting.vote.domain.AgendaHistory;
import com.example.residents_meeting.vote.domain.SelectOptionHistory;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationDTO;
import com.example.residents_meeting.vote.domain.dto.AgendaCreationResultDTO;
import com.example.residents_meeting.vote.domain.dto.SelectOptionCreationDto;
import com.example.residents_meeting.vote.exception.VoteException;
import com.example.residents_meeting.vote.exception.VoteExceptionCode;
import com.example.residents_meeting.vote.service.AgendaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(AgendaController.class)
class AgendaControllerTest {
	@MockBean
	private AgendaService agendaService;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@WithMockUser(roles = "MEMBER")
	void createAgenda() throws Exception {
		//given
		given(agendaService.createAgenda(any(AgendaCreationDTO.class)))
				.willReturn(new AgendaCreationResultDTO(
						"A12345678",
						"제목",
						"설명",
						LocalDate.now().plusDays(3),
						List.of("찬성", "반대")
				));

		//when & then
		mockMvc.perform(
				post("/api/agenda/").content(objectMapper.writeValueAsString(
						new AgendaCreationDTO(
								"A12345678",
								"제목",
								"설명",
								LocalDate.now().plusDays(3),
								List.of(
										new SelectOptionCreationDto("찬성", "찬성입니다."),
										new SelectOptionCreationDto("반대", "반대입니다.")
								)
						)
				)).contentType(MediaType.APPLICATION_JSON).with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.apartmentCode").value("A12345678"))
				.andExpect(jsonPath("$.title").value("제목"))
				.andExpect(jsonPath("$.details").value("설명"))
				.andExpect(jsonPath("$.endDate").value(LocalDate.now().plusDays(3).toString()))
				.andExpect(jsonPath("$.selectOptionSummaryList").isArray())
				.andExpect(jsonPath("$.selectOptionSummaryList[0]").value("찬성"))
				.andExpect(jsonPath("$.selectOptionSummaryList[1]").value("반대"))
				.andExpect(jsonPath("$.selectOptionSummaryList[2]").doesNotExist())
				.andDo(print());
	}

	@Test
	@WithMockUser(roles = "MEMBER")
	void getAgendaHistorySuccess() throws Exception {
		//given
		given(agendaService.getAgendaHistory(anyLong()))
				.willReturn(
						new AgendaHistory(
						"제목",
						"설명",
						LocalDate.now().minusDays(1),
						List.of(
								new SelectOptionHistory("찬성", "찬성입니다.",1),
								new SelectOptionHistory("반대", "반대입니다.",2)
								)
										)
							);

		//when & then
		mockMvc.perform(get("/api/agenda/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("제목"))
				.andExpect(jsonPath("$.details").value("설명"))
				.andExpect(jsonPath("$.endDate").value(LocalDate.now().minusDays(1).toString()))
				.andExpect(jsonPath("$.selectOptions").isArray())
				.andExpect(jsonPath("$.selectOptions[0].summary").value("찬성"))
				.andExpect(jsonPath("$.selectOptions[0].count").value(1))
				.andExpect(jsonPath("$.selectOptions[1].summary").value("반대"))
				.andExpect(jsonPath("$.selectOptions[1].count").value(2))
				.andDo(print());
	}

	@Test
	@WithMockUser(roles = "MEMBER")
	void getAgendaHistory_AgendaNotFoundException() throws Exception {
		//given
		doThrow(new VoteException(VoteExceptionCode.AGENDA_NOT_FOUND))
				.when(agendaService).getAgendaHistory(anyLong());

		//when & then
		mockMvc.perform(get("/api/agenda/1"))
				.andExpect(status().is(VoteExceptionCode.AGENDA_NOT_FOUND.getStatusCode()))
				.andExpect(jsonPath("$").value(VoteExceptionCode.AGENDA_NOT_FOUND.getMessage()))
				.andDo(print());
	}

	@Test
	@WithMockUser(roles = "MEMBER")
	void getAgendaHistory_OngoingSecretVoteException() throws Exception {
		//given
		doThrow(new VoteException(VoteExceptionCode.ONGOING_SECRET_VOTE))
				.when(agendaService).getAgendaHistory(anyLong());

		//when & then
		mockMvc.perform(get("/api/agenda/1"))
				.andExpect(status().is(VoteExceptionCode.ONGOING_SECRET_VOTE.getStatusCode()))
				.andExpect(jsonPath("$").value(VoteExceptionCode.ONGOING_SECRET_VOTE.getMessage()))
				.andDo(print());
	}

	@Test
	@WithMockUser(roles = "MEMBER")
	void getListOfUserOfSelectOption() throws Exception {
		//given
		given(agendaService.getListOfUserIdOfAgendaAndSelectOption(anyLong(), anyLong()))
				.willReturn(List.of(1L, 2L, 3L));
		//when
		mockMvc.perform(get("/api/agenda/2/select-option/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0]").value(1L))
				.andExpect(jsonPath("$[1]").value(2L))
				.andExpect(jsonPath("$[2]").value(3L))
				.andDo(print());
	}
}