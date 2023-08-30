package com.example.vote_service.repository.select_option;

import com.example.vote_service.domain.dto.SelectOptionVo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SelectOptionCustomRepository {
	Flux<SelectOptionVo> findAllByAgendaId(Long agendaId);
	Mono<SelectOptionVo> findById(Long id);

}
