package com.example.vote_service.repository.select_option;

import com.example.vote_service.domain.dto.SelectOptionVo;
import io.r2dbc.spi.Readable;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.function.Function;

@Repository
public class SelectOptionCustomRepositoryImpl implements SelectOptionCustomRepository {
	private static final Function<Readable, SelectOptionVo> MAPPER =
			result ->
				SelectOptionVo.builder()
						.id(result.get("sId", Long.class))
						.summary(result.get("sSummary", String.class))
						.details(result.get("sDetails", String.class))
						.createdAt(result.get("sCreatedTime", ZonedDateTime.class) == null ?
								null : result.get("sCreatedTime", ZonedDateTime.class).toLocalDateTime())
						.updatedAt(result.get("sUpdatedTime", ZonedDateTime.class) == null?
								null : result.get("sUpdatedTime", ZonedDateTime.class).toLocalDateTime())
						.agendaId(result.get("aId", Long.class))
						.build()
			;

	private final DatabaseClient databaseClient;

	public SelectOptionCustomRepositoryImpl(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
	}

	@Override
	public Mono<SelectOptionVo> findById(Long id) {
		String query = """
    			SELECT s.created_time as sCreatedTime, s.id as sId, s.updated_time as sUpdatedTime, 
    			s.summary as sSummary, s.details as sDetails,s.agenda_id as aId
				FROM select_option s 
				WHERE s.id = :id
				""";

		return databaseClient.sql(query)
				.bind("id", id)
				.<SelectOptionVo>map(MAPPER)
				.one();
	}
}
