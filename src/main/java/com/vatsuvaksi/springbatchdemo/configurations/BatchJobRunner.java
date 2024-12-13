package com.vatsuvaksi.springbatchdemo.configurations;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BatchJobRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job myBatchJob;

    @Autowired
    public BatchJobRunner(JobLauncher jobLauncher, Job myBatchJob) {
        this.jobLauncher = jobLauncher;
        this.myBatchJob = myBatchJob;
    }

    @Override
    public void run(String... args) throws Exception {
        jobLauncher.run(myBatchJob, new JobParameters());  // Start the job when the app starts
    }
}
