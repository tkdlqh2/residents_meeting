package com.example.user_service.domain;

import com.example.user_service.domain.dto.UserDto;
import com.example.user_service.domain.dto.UserSignUpRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Table(name = "USERS")
public class User extends BaseEntity implements UserDetails {
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


	protected User() {
	}

	protected User(Long id,
				   String email,
				   String password,
				   String name,
				   String phone,
				   Address address,
				   UserRole role) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.name = name;
		this.phone = phone;
		this.address = address;
		this.role = role;
	}

	public static User fromSignUpRequestAndPasswordEncoder(UserSignUpRequest signUpRequest, PasswordEncoder passwordEncoder) {
		return new User(null,
				signUpRequest.email(),
				passwordEncoder.encode(signUpRequest.password()),
				signUpRequest.name(),
				signUpRequest.phone(),
				new Address(signUpRequest.apartmentCode(), signUpRequest.building(), signUpRequest.unit()),
				UserRole.UNREGISTERED);
	}

	protected void setId(Long id) {
		this.id = id;
	}

	public UserDto toUserDto() {
		return new UserDto(email, name);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(this.role.name()));
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
