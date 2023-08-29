package com.example.user_service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@TestConfiguration
@EnableWebSecurity
public class TestWebSecurity {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(request ->
						request.requestMatchers(antMatcher("/actuator/**")).permitAll()
								.requestMatchers(antMatcher("/h2-console/**")).permitAll()
								.requestMatchers(antMatcher("/api/user/**")).permitAll()
								.requestMatchers(antMatcher("**exception**")).permitAll()
								.anyRequest().authenticated()
				)
				.headers(header -> header.frameOptions(
						HeadersConfigurer.FrameOptionsConfig::sameOrigin));
		return http.build();
	}
}
