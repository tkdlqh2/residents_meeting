package com.example.residents_meeting.common;

import com.example.residents_meeting.user.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Getter
@Setter
public class RequestContextHolder {
	private User user;
}