package com.example.user_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
		return ResponseEntity.status(500).body(e.getMessage());
	}

	@ExceptionHandler(UserException.class)
	public ResponseEntity<String> handleUserException(UserException e) {
		return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
	}
}


