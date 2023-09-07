package com.example.vote_service.otherservice;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
	private final WebClient webClient;

	public UserServiceImpl(WebClient webClient) {
		this.webClient = webClient;
	}

	@Override
	public Mono<List> getUserEmails(List<Long> userIds) {
		String userIdsString = userIds.stream().map(String::valueOf)
				.collect(Collectors.joining(","));

		return webClient.get()
				.uri("http://localhost:8080/api/user/" + userIdsString)
				.retrieve()
				.bodyToMono(List.class);
	}
}
