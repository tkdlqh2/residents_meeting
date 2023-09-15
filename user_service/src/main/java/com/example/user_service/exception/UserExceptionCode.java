package com.example.user_service.exception;

import lombok.Getter;

@Getter
public enum UserExceptionCode {

	PASSWORD_NOT_MATCH(400, "Password not match"),
	EMAIL_ALREADY_EXIST(400, "Email already exist"),
	PHONE_ALREADY_EXIST(400, "Phone already exist"),
	LEADER_ALREADY_EXIST(400,"leader already exist"),
	TOKEN_ALREADY_EXIST(400,"token already exist" ),
	TOKEN_NOT_FOUND(400, "token not found"),
	EXPIRED_TOKEN(400,"expired token" ),
	ADDRESS_NOT_MATCH(400, "address not match" ),
	TOO_MANY_VICE_LEADER(400,"there are too many vice leaders" ),
	USER_NOT_FOUND(400, "user not found" );
	private final int statusCode;
	private final String message;

	UserExceptionCode(int statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}
}
