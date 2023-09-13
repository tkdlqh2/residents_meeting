package com.example.scheduler_and_consumer.batch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Getter
@Setter
@Component
public class JobParameterHolder {
	private LocalDate targetDate;
}
