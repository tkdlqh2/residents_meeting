package com.example.vote_service.repository;

import com.example.vote_service.domain.SelectOptionHistory;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SelectOptionHistoryRepository extends R2dbcRepository<SelectOptionHistory, Long> {
	@Query("SELECT select_option_history.count FROM select_option_history WHERE select_option_history.id = :selectOptionId")
	Mono<Integer> findCountById(Long selectOptionId);
}
