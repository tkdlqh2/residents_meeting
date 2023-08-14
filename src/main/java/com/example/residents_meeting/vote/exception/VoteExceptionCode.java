package com.example.residents_meeting.vote.exception;

import lombok.Getter;

@Getter
public enum VoteExceptionCode {
	SELECT_OPTION_NOT_FOUND(404, "Select option not found"),
	NO_RIGHT_FOR_VOTE(400, "This user cannot vote for this agenda");

	VoteExceptionCode(int statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}

	private final int statusCode;
	private final String message;
}
