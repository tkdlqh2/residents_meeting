package com.example.residents_meeting.vote.exception;

import lombok.Getter;

@Getter
public enum VoteExceptionCode {
	AGENDA_NOT_FOUND(404, "Agenda not found"),
	SELECT_OPTION_NOT_FOUND(404, "Select option not found"),
	ONGOING_SECRET_VOTE(400, "This agenda is ongoing secret vote"),
	NO_RIGHT_FOR_VOTE(400, "This user cannot vote for this agenda");

	VoteExceptionCode(int statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}

	private final int statusCode;
	private final String message;
}
