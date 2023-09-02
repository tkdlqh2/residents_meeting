package com.example.vote_service.repository.select_option;import com.example.vote_service.domain.SelectOption;import org.springframework.data.jpa.repository.Query;import org.springframework.data.r2dbc.repository.R2dbcRepository;import org.springframework.stereotype.Repository;import reactor.core.publisher.Mono;import java.util.List;@Repositorypublic interface SelectOptionRepository extends R2dbcRepository<SelectOption, Long> {	@Query(""" 			SELECT COUNT(v.userId)			FROM Vote v			WHERE (v.userId, v.createdTime) IN (				SELECT userId, MAX(createdTime)				FROM Vote				GROUP BY userId				HAVING v.selectOptionId IN (SELECT s.id FROM SelectOption s WHERE s.id = :selectOptionId)			)			GROUP BY v.userId			HAVING v.selectOptionId = :selectOptionId			     """)	Mono<Integer> countById(Long selectOptionId);	@Query(""" 			SELECT v.userId			FROM Vote v			WHERE (v.userId, v.createdTime) IN (				SELECT userId, MAX(createdTime)				FROM Vote				GROUP BY userId				HAVING v.selectOptionId in (SELECT s.id FROM SelectOption s WHERE s.agendaId = :agendaId)			)			GROUP BY v.userId			HAVING v.selectOptionId = :selectOptionId			     """)	Mono<List<Long>> findUserIdsByAgendaIdAndId(Long agendaId, Long selectOptionId);}