package com.example.vote_service.repository.agenda;

import com.example.vote_service.domain.dto.AgendaVo;
import com.example.vote_service.domain.dto.SelectOptionVo;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

@Repository
public class AgendaCustomRepositoryImpl implements AgendaCustomRepository {

	private final DatabaseClient databaseClient;

	public AgendaCustomRepositoryImpl(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
	}

	@Override
	public Mono<AgendaVo> findByIdUsingFetchJoin(Long id) {

		String sqlWithSelectOption =
    		"""
    			SELECT a.id AS agendaId, a.apartment_code AS apartmentCode, a.title AS agendaTitle, 
    			a.details AS agendaDetails, a.end_date AS agendaEndDate, a.created_time AS agendaCreatedTime,
    			a.updated_time AS agendaUpdatedTime, s.id AS selectOptionId, s.summary AS selectOptionSummary,
    			s.details AS selectOptionDetails, s.created_time AS selectOptionCreatedTime
				FROM Agenda a 
				JOIN select_option s
				ON a.id = s.agenda_id
				WHERE a.id = :id
			""";

		return databaseClient.sql(sqlWithSelectOption)
				.fetch().all()
				.sort(Comparator.comparing(result -> (Long) result.get("selectOptionId")))
				.collectList()
				.map(result -> {
					var selectOptions = result.stream()
							.map(row -> new SelectOptionVo(
									(Long) row.get("selectOptionId"),
									null,
									(String) row.get("selectOptionSummary"),
									(String) row.get("selectOptionDetails"),
									(LocalDateTime) row.get("selectOptionCreatedTime"),
									null
							)).toList();
					var row = result.get(0);

					return AgendaVo.builder()
							.id((Long) row.get("agendaId"))
							.apartmentCode((String) row.get("apartmentCode"))
							.title((String) row.get("agendaTitle"))
							.details((String) row.get("agendaDetails"))
							.endDate((LocalDate) row.get("agendaEndDate"))
							.createdAt((LocalDateTime) row.get("agendaCreatedTime"))
							.updatedAt((LocalDateTime) row.get("agendaUpdatedTime"))
							.selectOptionList(selectOptions)
							.build();
				});
	}

	@Override
	public Mono<LocalDate> findEndDateById(Long agendaId) {
		String sql = """
				SELECT a.end_date
				FROM agenda a
				WHERE id = :agendaId
				""";

		return databaseClient.sql(sql)
				.bind("agendaId", agendaId)
				.fetch().first()
				.map(result -> (LocalDate) result.get("end_date"));
	}
}
