package com.example.scheduler_and_consumer.batch;


import com.example.scheduler_and_consumer.domain.Agenda;
import com.example.scheduler_and_consumer.domain.AgendaHistory;
import com.example.scheduler_and_consumer.domain.SelectOption;
import com.example.scheduler_and_consumer.domain.SelectOptionHistory;
import com.example.scheduler_and_consumer.repository.VoteRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class BatchConfig {

	private final EntityManagerFactory entityManagerFactory;
	private final VoteRepository voteRepository;
	private final JobParameterHolder jobParameterHolder;
	private static final int CHUNK_SIZE = 10;

	@Bean
	public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new JobBuilder("voteHistoryJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(agendaHistoryStep(jobRepository,
						transactionManager,
						agendaHistoryTargetReader(),
						agendaHistoryProcessor(),
						agendaHistoryItemWriter()))
				.next(selectOptionHistoryStep(jobRepository,
						transactionManager,
						selectOptionHistoryTargetReader(),
						selectOptionHistoryProcessor(voteRepository),
						selectOptionHistoryItemWriter()))
				.build();
	}

	@Bean
	protected Step agendaHistoryStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, ItemReader<Agenda> reader,
									 ItemProcessor<Agenda, AgendaHistory> processor, ItemWriter<AgendaHistory> writer) {
		return new StepBuilder("agendaHistoryStep", jobRepository).<Agenda, AgendaHistory> chunk(CHUNK_SIZE, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}

	@Bean
	protected Step selectOptionHistoryStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, ItemReader<SelectOption> reader,
									 ItemProcessor<SelectOption, SelectOptionHistory> processor, ItemWriter<SelectOptionHistory> writer) {
		return new StepBuilder("selectOptionHistoryStep", jobRepository).<SelectOption, SelectOptionHistory> chunk(CHUNK_SIZE, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}


	@Bean
	@StepScope
	public JpaPagingItemReader<Agenda> agendaHistoryTargetReader() {

		Map<String, Object> parameters = Map.of("targetDate", jobParameterHolder.getTargetDate());

		return new JpaPagingItemReaderBuilder<Agenda>()
				.name("agendaHistoryTargetReader")
				.queryString("""
				SELECT a FROM Agenda a
   				WHERE a.endDate = :targetDate
				""")
				.parameterValues(parameters)
				.pageSize(10)
				.entityManagerFactory(entityManagerFactory)
				.build();
	}

	@Bean
	@StepScope
	public ItemProcessor<Agenda, AgendaHistory> agendaHistoryProcessor()  {
		return AgendaHistory::from;
	}

	@Bean
	@StepScope
	public JpaItemWriter<AgendaHistory> agendaHistoryItemWriter() {
		JpaItemWriter<AgendaHistory> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}


	@Bean
	@StepScope
	public JpaPagingItemReader<SelectOption> selectOptionHistoryTargetReader() {

		Map<String, Object> parameters = Map.of("targetDate", jobParameterHolder.getTargetDate());

		return new JpaPagingItemReaderBuilder<SelectOption>()
				.name("agendaHistoryTargetReader")
				.queryString("""
				SELECT s FROM SelectOption s
				JOIN Agenda a ON s.agenda.id = a.id
   				WHERE a.endDate = :targetDate
				""")
				.pageSize(10)
				.parameterValues(parameters)
				.entityManagerFactory(entityManagerFactory)
				.build();
	}

	@Bean
	@StepScope
	public ItemProcessor<SelectOption, SelectOptionHistory> selectOptionHistoryProcessor(VoteRepository voteRepository)  {
		return selectOption -> {
			Integer count = voteRepository.countLastById(selectOption.getId());
			count = count == null ? 0 : count;
			List<Long> userIds = voteRepository.findUserIdBySelectOptionId(selectOption.getId());
			return SelectOptionHistory.from(selectOption, count, userIds);
		};
	}

	@Bean
	@StepScope
	public JpaItemWriter<SelectOptionHistory> selectOptionHistoryItemWriter() {
		JpaItemWriter<SelectOptionHistory> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}
}
