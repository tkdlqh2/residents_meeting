package com.example.residents_meeting.user.domain;

import com.example.residents_meeting.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;

@Entity
@Getter
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Email
	@Column(unique = true, nullable = false)
	private String email;
	@Column(nullable = false)
	private String password;
	@Column(nullable = false)
	private String name;
	@Column(unique = true, nullable = false)
	private String phone;
	@Embedded
	@Column(unique = true, nullable = false)
	private Address address;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private UserRole role;
}
