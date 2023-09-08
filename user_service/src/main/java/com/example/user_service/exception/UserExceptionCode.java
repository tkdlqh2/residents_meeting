package com.example.user_service.exception;

import lombok.Getter;

@Getter
public enum UserExceptionCode {

	PASSWORD_NOT_MATCH(400, "Password not match"),
	EMAIL_ALREADY_EXIST(400, "Email already exist"),
	PHONE_ALREADY_EXIST(400, "Phone already exist");
	private final int statusCode;
	private final String message;

	UserExceptionCode(int statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}
}
