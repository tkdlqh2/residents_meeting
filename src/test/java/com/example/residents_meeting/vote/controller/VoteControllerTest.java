package com.example.residents_meeting.vote.controller;

import com.example.residents_meeting.vote.domain.dto.VoteCreationDto;
import com.example.residents_meeting.vote.domain.dto.VoteCreationResultDto;
import com.example.residents_meeting.vote.exception.VoteException;
import com.example.residents_meeting.vote.exception.VoteExceptionCode;
import com.example.residents_meeting.vote.service.VoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(VoteController.class)
class VoteControllerTest {
	private static final Long AGENDA_ID = 1L;
	private static final Long SELECT_OPTION_ID = 2L;
	@MockBean
	private VoteService voteService;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("투표 성공")
	void voteSuccess() throws Exception {

		//given

		given(voteService.createVote(any(VoteCreationDto.class)))
				.willReturn(new VoteCreationResultDto("title", "summary"));

		//when & then
		mockMvc.perform(
					post("/api/vote/")
					.content(objectMapper.writeValueAsString(
							new VoteCreationDto(AGENDA_ID, SELECT_OPTION_ID)
					))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.agendaTitle").value("title"))
				.andExpect(jsonPath("$.selectOptionSummary").value("summary"))
				.andDo(print())
				;
	}

	@Test
	@DisplayName("투표 실패 - 선택지 찾을 수 없음")
	void voteFail_SelectOptionNotFound() throws Exception {
		//given
		doThrow(new VoteException(VoteExceptionCode.SELECT_OPTION_NOT_FOUND))
				.when(voteService).createVote(any(VoteCreationDto.class));

		//when & then
		mockMvc.perform(
						post("/api/vote/")
								.content(objectMapper.writeValueAsString(
										new VoteCreationDto(AGENDA_ID, SELECT_OPTION_ID)
								))
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(VoteExceptionCode.SELECT_OPTION_NOT_FOUND.getStatusCode()))
				.andExpect(jsonPath("$").value(VoteExceptionCode.SELECT_OPTION_NOT_FOUND.getMessage()))
				.andDo(print());
	}

	@Test
	@DisplayName("투표 실패 - 투표 권한 없음")
	void voteFail_NoRightForVote() throws Exception {
		//given
		doThrow(new VoteException(VoteExceptionCode.NO_RIGHT_FOR_VOTE))
				.when(voteService).createVote(any(VoteCreationDto.class));

		//when & then
		mockMvc.perform(
						post("/api/vote/")
								.content(objectMapper.writeValueAsString(
										new VoteCreationDto(AGENDA_ID, SELECT_OPTION_ID)
								))
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(VoteExceptionCode.NO_RIGHT_FOR_VOTE.getStatusCode()))
				.andExpect(jsonPath("$").value(VoteExceptionCode.NO_RIGHT_FOR_VOTE.getMessage()))
				.andDo(print());
	}
}