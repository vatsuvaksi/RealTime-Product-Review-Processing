package com.vatsuvaksi.springbatchdemo.configurations;


import com.vatsuvaksi.springbatchdemo.entities.ProductReview;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager transactionManager;

    @Autowired
    @StepScope
    private Reader reader;

    @Autowired
    @StepScope
    private NoOpItemProcessor processor;

    @Autowired
    @StepScope
    private Writer writer;

    @Autowired
    private Partitioner partitioner;

    @Autowired
    private JobCompletionNotificationListener listener;

    private final int chunkSize = 5000;

    @Bean
    public Job job(){
        return new JobBuilder("batchJobdemo" , jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(leaderStep())
                .listener(listener)
                .build();
    }

    @Bean
    public Step leaderStep() {
        return new StepBuilder("leaderStep" , jobRepository)
                .partitioner("workerStep" , partitioner)
                .step(workerStep())
                .taskExecutor(taskExecutor())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step workerStep() {
        return new StepBuilder("workerStep", jobRepository)
                .<ProductReview, ProductReview>chunk(chunkSize, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        int cores = Runtime.getRuntime().availableProcessors();
        taskExecutor.setCorePoolSize(Math.max(cores - 1, 1));
        taskExecutor.setMaxPoolSize(Math.max(cores - 1, 1));
        taskExecutor.setQueueCapacity(64);
        taskExecutor.setThreadNamePrefix("BatchTask-");
        taskExecutor.initialize();
        return taskExecutor;
    }
}
