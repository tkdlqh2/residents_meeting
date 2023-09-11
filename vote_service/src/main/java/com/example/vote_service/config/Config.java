package com.example.vote_service.config;

import com.example.vote_service.domain.AuthTokenHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Config {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public WebClient webClient() {
		return WebClient.builder().defaultHeader("Authorization","eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYmNAZ21haWwuY29tIiwicm9sZXMiOlsiTUVNQkVSIl0sImlhdCI6MTY5NDI2OTU1NiwiZXhwIjoxNjk0MzU1OTU2fQ.h1QWdofPihFjFfdKMn7S8d-ZTy_N2WRWD8H7rSkNM9PKpwonChJPVutttu67qfqo32r1r4Oc47uGaFqK9AmDLA").build();
	}

	@RequestScope
	public RequestContextHolder requestContextHolder() {
		return new AuthTokenHolder();
	}
}
