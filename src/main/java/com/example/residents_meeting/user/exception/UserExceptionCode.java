package com.example.residents_meeting.user.exception;

import lombok.Getter;

@Getter
public enum UserExceptionCode {

	PASSWORD_NOT_MATCH(400, "Password not match"),
	EMAIL_ALREADY_EXIST(400, "Email already exist");
	private final int statusCode;
	private final String message;

	UserExceptionCode(int statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}
}
