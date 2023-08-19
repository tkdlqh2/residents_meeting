package com.example.residents_meeting.vote.repository;import com.example.residents_meeting.vote.domain.SelectOption;import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.data.jpa.repository.Query;import org.springframework.stereotype.Repository;import java.util.List;import java.util.Optional;@Repositorypublic interface SelectOptionRepository extends JpaRepository<SelectOption, Long> {	Optional<SelectOption> findByAgendaIdAndId(Long agendaId, Long id);	@Query(""" 	SELECT COUNT(v.user.id)	FROM Vote v	WHERE (v.user.id, v.createdTime) IN (		SELECT user.id, MAX(createdTime)		FROM Vote		GROUP BY user.id		HAVING selectOption.agenda.id =  (SELECT s.agenda.id FROM SelectOption s WHERE s.id = :selectOptionId)	)	GROUP BY v.user.id	HAVING v.selectOption.id = :selectOptionId      """)	Integer countById(Long selectOptionId);	@Query(""" 	SELECT v.user.id	FROM Vote v	WHERE (v.user.id, v.createdTime) IN (		SELECT user.id, MAX(createdTime)		FROM Vote		GROUP BY user.id		HAVING selectOption.agenda.id =  :agendaId	)	GROUP BY v.user.id	HAVING v.selectOption.id = :selectOptionId      """)	List<Long> findUserIdsByAgendaIdAndId(Long agendaId, Long selectOptionId);}