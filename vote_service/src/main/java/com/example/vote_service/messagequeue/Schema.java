package com.example.vote_service.messagequeue;

import lombok.Builder;

import java.util.List;

@Builder
public record Schema(
		String type,
		List<Field> fields,
		boolean optional,
		String name
) {
}
