package com.example.vote_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(VoteException.class)
	public Mono<ResponseEntity<String>> handleValidationException(VoteException ex) {
		return Mono.just(ResponseEntity.badRequest().body(ex.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public Mono<ResponseEntity<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
		return Mono.just(ResponseEntity.badRequest().body(ex.getMessage()));
	}
}