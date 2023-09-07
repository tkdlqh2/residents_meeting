package com.example.vote_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Config {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public WebClient webClient() {
		return WebClient.builder().filter(
				(request, next) -> next.exchange(
						org.springframework.web.reactive.function.client.ClientRequest.from(request)
								.headers(headers -> headers.add("Authorization", request.headers().getFirst("Authorization")))
								.build()
				)
		).build();
	}
}
