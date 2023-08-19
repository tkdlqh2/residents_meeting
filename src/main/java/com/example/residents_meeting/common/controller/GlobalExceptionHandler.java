package com.example.residents_meeting.common.controller;

import com.example.residents_meeting.user.exception.UserException;
import com.example.residents_meeting.vote.exception.VoteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e) {
		log.error("Exception: ", e);
		return ResponseEntity.status(500).body("Internal Server Error");
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
		log.error("IllegalArgumentException: ", e);
		return ResponseEntity.status(400).body(e.getMessage());
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException e) {
		log.debug("UsernameNotFoundException: ", e);
		return ResponseEntity.status(404).body(e.getMessage());
	}

	@ExceptionHandler(UserException.class)
	public ResponseEntity<String> handleUserException(UserException e) {
		log.debug("UserException: ", e);
		return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
	}

	@ExceptionHandler(VoteException.class)
	public ResponseEntity<String> handleVoteException(VoteException e) {
		log.debug("VoteException: ", e);
		return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
	}
}
