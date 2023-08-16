package com.example.residents_meeting.vote.repository;

import com.example.residents_meeting.vote.domain.Agenda;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest
class AgendaRepositoryTest {

	@Autowired
	private AgendaRepository agendaRepository;

	@Test
	void findByIdUsingFetchJoin() {
		//given
		Long agendaId = 1L;

		//when
		Optional<Agenda> optionalAgenda = agendaRepository.findByIdUsingFetchJoin(agendaId);

		//then
		assertTrue(optionalAgenda.isPresent());

	}
}