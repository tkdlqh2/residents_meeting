package com.example.user_service.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
	private final int statusCode;

	public UserException(UserExceptionCode userExceptionCode) {
		super(userExceptionCode.getMessage());
		this.statusCode = userExceptionCode.getStatusCode();
	}
}
