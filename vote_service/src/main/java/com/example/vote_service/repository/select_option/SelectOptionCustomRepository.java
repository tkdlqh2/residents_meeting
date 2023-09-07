package com.example.vote_service.repository.select_option;

import com.example.vote_service.domain.dto.SelectOptionVo;
import reactor.core.publisher.Mono;

public interface SelectOptionCustomRepository {
	Mono<SelectOptionVo> findById(Long id);

}
