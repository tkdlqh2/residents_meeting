package com.example.residents_meeting.user.security;

import com.example.residents_meeting.common.RequestContextHolder;
import com.example.residents_meeting.user.domain.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final UserDetailsService userDetailsService;
	private final JwtTokenProvider jwtTokenProvider;
	private final RequestContextHolder requestContextHolder;

	public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider, RequestContextHolder requestContextHolder) {
		this.userDetailsService = userDetailsService;
		this.jwtTokenProvider = jwtTokenProvider;
		this.requestContextHolder = requestContextHolder;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String token = jwtTokenProvider.resolveToken(request);
		if (token != null) {
			token = token.replace("Bearer ", "");
			if(jwtTokenProvider.validateToken(token)){
				String userName = jwtTokenProvider.getUsername(token);
				User user = (User) userDetailsService.loadUserByUsername(userName);
				SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities()));
				requestContextHolder.setUser(user);
			}
		}
		filterChain.doFilter(request, response);
	}
}
