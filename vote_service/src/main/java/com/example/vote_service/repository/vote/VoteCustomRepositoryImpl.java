package com.example.vote_service.repository.vote;

import com.example.vote_service.domain.dto.VoteHistory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
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
			SELECT s.id as id, v.created_time as createdTime
			FROM Vote as v
				INNER JOIN select_option s ON v.select_option_id = s.id
			WHERE s.agenda_id = :agendaId
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

	@Override
	public Mono<Integer> findVoteCountOfSelectOptionId(Long agendaId, Long selectOptionId) {

		String sql = """
			SELECT count(v.user_id) as voteCount
			FROM Vote as v
			WHERE (v.user_id, v.created_time) IN (
				SELECT v2.user_id, MAX(v2.created_time)
				FROM Vote v2
				WHERE v2.select_option_id IN (
					SELECT s.id FROM select_option s WHERE s.agenda_id = :agendaId
				)
				GROUP BY v2.user_id
			)
			GROUP BY v.select_option_id
			HAVING v.select_option_id = :selectOptionId
			""";

		return databaseClient.sql(sql)
				.bind("agendaId",agendaId)
				.bind("selectOptionId",selectOptionId)
				.map(row -> row.get("voteCount", Integer.class))
				.one();
	}

	@Override
	public Flux<Long> findUserIdsByAgendaIdAndId(Long agendaId, Long selectOptionId) {

		String sql = """
			SELECT v.user_id as userId
			FROM Vote as v
			WHERE (v.user_id, v.created_time) IN (
				SELECT v2.user_id, MAX(v2.created_time)
				FROM Vote v2
				WHERE v2.select_option_id IN (
					SELECT s.id FROM select_option s WHERE s.agenda_id = :agendaId
				)
				GROUP BY v2.user_id
			)
			AND v.select_option_id = :selectOptionId
			 """;
		return databaseClient.sql(sql)
				.bind("agendaId",agendaId)
				.bind("selectOptionId",selectOptionId)
				.map(row -> row.get("userId", Long.class))
				.all();
	}


}
