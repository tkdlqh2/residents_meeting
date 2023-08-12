package com.example.residents_meeting.user.domain;

import com.example.residents_meeting.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;

@Entity
@Getter
@Table(name = "USERS")
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


	public User() {
	}
	public User(String email, String password, String name, String phone, Address address, UserRole role) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.phone = phone;
		this.address = address;
		this.role = role;
	}

	protected void setId(Long id) {
		this.id = id;
	}


}
