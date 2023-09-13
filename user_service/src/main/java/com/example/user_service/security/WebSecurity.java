package com.example.user_service.security;

import com.example.user_service.config.RequestContextHolder;
import com.example.user_service.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurity {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserService userDetailsService;
	private final RequestContextHolder requestContextHolder;

	public WebSecurity(JwtTokenProvider jwtTokenProvider, UserService userDetailsService, RequestContextHolder requestContextHolder) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.userDetailsService = userDetailsService;
		this.requestContextHolder = requestContextHolder;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(request ->
						request.requestMatchers(antMatcher("/actuator/**")).permitAll()
								.requestMatchers(antMatcher("/h2-console/**")).permitAll()
								.requestMatchers(antMatcher("/api/user/sign-up")).permitAll()
								.requestMatchers(antMatcher("/api/user/login")).permitAll()
								.requestMatchers(antMatcher("/swagger-ui/**")).permitAll()
								.requestMatchers(antMatcher("/v3/api-docs/**")).permitAll()
								.requestMatchers(antMatcher("/swagger-resources/**")).permitAll()
								.requestMatchers(antMatcher("/error")).permitAll()
								.anyRequest().authenticated()
				)
				.headers(header -> header.frameOptions(
						HeadersConfigurer.FrameOptionsConfig::sameOrigin))
				.addFilterBefore(new JwtAuthenticationFilter(userDetailsService, jwtTokenProvider, requestContextHolder), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}