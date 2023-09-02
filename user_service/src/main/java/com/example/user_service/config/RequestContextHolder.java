package com.example.user_service.config;

import com.example.user_service.domain.dto.UserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestContextHolder {
	private UserInfo userInfo;

}
