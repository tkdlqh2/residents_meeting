package com.example.vote_service.repository.select_option;

import com.example.vote_service.domain.Agenda;
import com.example.vote_service.domain.dto.SelectOptionVo;
import io.r2dbc.spi.Readable;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.function.Function;

@Repository
public class SelectOptionCustomRepositoryImpl implements SelectOptionCustomRepository {
	private static final Function<Readable, SelectOptionVo> MAPPER =
			result -> {
				Agenda agenda = Agenda.builder()
						.id(result.get("aId", Long.class))
						.title(result.get("aTitle", String.class))
						.details(result.get("aDetails", String.class))
						.endDate(result.get("aEndDate", LocalDate.class))
						.apartmentCode(result.get("aApartmentCode", String.class))
						.createdAt(result.get("aCreatedTime", ZonedDateTime.class).toLocalDateTime())
						.updatedAt(result.get("aUpdatedTime", ZonedDateTime.class).toLocalDateTime())
						.build();

				SelectOptionVo selectOption = SelectOptionVo.builder()
						.id(result.get("sId", Long.class))
						.agenda(agenda)
						.summary(result.get("sSummary", String.class))
						.details(result.get("sDetails", String.class))
						.createdAt(result.get("sCreatedTime", ZonedDateTime.class).toLocalDateTime())
						.updatedAt(result.get("sUpdatedTime", ZonedDateTime.class).toLocalDateTime())
						.build();

				return selectOption;
			};

	private final DatabaseClient databaseClient;

	public SelectOptionCustomRepositoryImpl(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
	}

	@Override
	public Flux<SelectOptionVo> findAllByAgendaId(Long agendaId) {

		String query = """
    			SELECT s.created_time as sCreatedTime, s.id as sId, s.updated_time as sUpdatedTime, 
    			s.summary as sSummary, s.details as sDetails,
    			a.created_time as aCreatedTime, a.apartment_code as aApartmentCode, a.details as aDetails, 
    			a.end_date as aEndDate, a.id as aId, a.title as aTitle, a.updated_time as aUpdatedTime 
				FROM select_option s 
				JOIN agenda a ON s.agenda_id= a.id
				HAVING s.agenda_id = :agendaId
				""";


		return databaseClient.sql(query)
				.bind("agendaId", agendaId)
				.map(MAPPER::apply)
				.all();
	}

	@Override
	public Mono<SelectOptionVo> findById(Long id) {
		String query = """
    			SELECT s.created_time as sCreatedTime, s.id as sId, s.updated_time as sUpdatedTime, 
    			s.summary as sSummary, s.details as sDetails,
    			a.created_time as aCreatedTime, a.apartment_code as aApartmentCode, a.details as aDetails, 
    			a.end_date as aEndDate, a.id as aId, a.title as aTitle, a.updated_time as aUpdatedTime 
				FROM select_option s 
				JOIN agenda a ON s.agenda_id = a.id
				HAVING s.id = :id
				""";

		return databaseClient.sql(query)
				.bind("id", id)
				.map(MAPPER::apply)
				.one();
	}
}
