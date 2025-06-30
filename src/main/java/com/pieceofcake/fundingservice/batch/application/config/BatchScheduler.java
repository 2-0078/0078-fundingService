package com.pieceofcake.fundingservice.batch.application.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class BatchScheduler {
    private final JobLauncher jobLauncher;
    private final Job dailyFundingJob;

    //임시 시간 설정
    @Scheduled(cron = "0 0/30 * * * *")
    public void runDailyFundingJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("time", LocalDateTime.now().toString())
                .addString("jobName", "dailyFundingJob")
                .addString("date", LocalDate.now().toString())
                .toJobParameters();

        jobLauncher.run(dailyFundingJob, jobParameters);
    }
}
