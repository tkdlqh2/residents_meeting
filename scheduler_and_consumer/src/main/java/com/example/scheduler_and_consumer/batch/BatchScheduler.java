package com.example.scheduler_and_consumer.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchScheduler {

	private final JobLauncher jobLauncher;
	private final BatchConfig batchConfig;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final JobParameterHolder jobParameterHolder;

	@Scheduled(cron = "${batch.schedule.cron}")
	public void runJob() {

		LocalDate targetDate = LocalDate.now().minusDays(1);

		JobParameters jobParameters = new JobParametersBuilder()
				.addLocalDate("today", targetDate)
				.toJobParameters();
		jobParameterHolder.setTargetDate(targetDate);

		try{
			jobLauncher.run(batchConfig.job(jobRepository, transactionManager), jobParameters);
		} catch (JobInstanceAlreadyCompleteException |
				 JobExecutionAlreadyRunningException |
				 JobParametersInvalidException |
				 JobRestartException e
		 ) {
			throw new RuntimeException(e);
		}
	}
}
