package com.example.residents_meeting.vote.domain;

import com.example.residents_meeting.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Vote {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	private SelectOption selectOption;

	@ManyToOne(optional = false)
	private User user;


}
