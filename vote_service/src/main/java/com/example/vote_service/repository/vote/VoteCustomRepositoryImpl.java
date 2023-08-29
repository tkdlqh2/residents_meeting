package com.example.vote_service.repository.vote;

import com.example.vote_service.domain.dto.VoteHistory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public class VoteCustomRepositoryImpl implements VoteCustomRepository {

	private final DatabaseClient databaseClient;

	public VoteCustomRepositoryImpl(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
	}

	@Override
	public Mono<VoteHistory> findVoteHistoryByUserIdAndAgendaId(Long userId, Long agendaId) {

		String sql = """ 
			SELECT s.id, v.created_time
			FROM Vote as v
				INNER JOIN select_option s ON v.select_option_id = s.id
			HAVING s.agenda_id = :agendaId
				AND v.user_id = :userId
			ORDER BY v.created_time DESC
			LIMIT 1
			     """;

		return databaseClient.sql(sql)
				.bind("userId",userId)
				.bind("agendaId",agendaId)
				.map(row -> new VoteHistory(row.get("id", Long.class),
						row.get("createdTime", LocalDateTime.class)))
				.first();
	}
}
