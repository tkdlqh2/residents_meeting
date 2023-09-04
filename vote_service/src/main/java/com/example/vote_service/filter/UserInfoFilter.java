package com.example.vote_service.filter;

import com.example.vote_service.UserInfo;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class UserInfoFilter implements WebFilter {

	private final RestTemplate restTemplate;
	public UserInfoFilter(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", exchange.getRequest().getHeaders().getFirst("Authorization"));

		UserInfo userInfo = restTemplate.exchange("http://localhost:8000/api/user/",
				HttpMethod.GET,
				new HttpEntity<>(headers),
				UserInfo.class)
				.getBody();

		return chain.filter(exchange).contextWrite(context -> context.put("user", userInfo));
	}
}