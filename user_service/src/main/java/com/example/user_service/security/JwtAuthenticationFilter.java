package com.example.user_service.security;

import com.example.user_service.config.RequestContextHolder;
import com.example.user_service.domain.User;
import com.example.user_service.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final UserService userService;
	private final JwtTokenProvider jwtTokenProvider;
	private final RequestContextHolder requestContextHolder;

	public JwtAuthenticationFilter(UserService userService, JwtTokenProvider jwtTokenProvider, RequestContextHolder requestContextHolder) {
		this.userService = userService;
		this.jwtTokenProvider = jwtTokenProvider;
		this.requestContextHolder = requestContextHolder;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String token = jwtTokenProvider.resolveToken(request);
		if (token != null) {
			token = token.replace("Bearer ", "");
			if (jwtTokenProvider.validateToken(token)) {
				String userName = jwtTokenProvider.getUsername(token);
				User user = (User) userService.loadUserByUsername(userName);
				SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities()));
				requestContextHolder.setUserInfo(user.toUserInfo());
			}
		}
		filterChain.doFilter(request, response);
	}
}
