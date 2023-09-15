package com.example.user_service.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;


@JsonDeserialize(using = UserRole.UserRoleDeserializer.class)
public enum UserRole {
	ADMIN, LEADER, VICE_LEADER, HOUSE_LEADER, MEMBER, UNREGISTERED;

	private static final Map<String, UserRole> USER_ROLE_MAP =
			Arrays.stream(UserRole.values())
					.collect(Collectors.toMap(
							Enum::toString,
							userRole -> userRole
					));

	public static UserRole getUserRole(String userRole) {
		return USER_ROLE_MAP.get(userRole);
	}

	static class UserRoleDeserializer extends StdDeserializer<UserRole> {
		public UserRoleDeserializer() {
			super(UserRole.class);
		}

		@Override
		public UserRole deserialize(JsonParser p, DeserializationContext ctxt) throws IOException{
			JsonNode node = p.getCodec().readTree(p);
			return UserRole.getUserRole(node.get(0).asText());
		}
	}
}
