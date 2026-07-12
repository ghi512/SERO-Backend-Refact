package com.werp.sero.system.batch.config;

import com.werp.sero.production.command.domain.aggregate.ProductionRequest;
import com.werp.sero.production.command.domain.aggregate.ProductionRequestItem;
import com.werp.sero.system.command.domain.aggregate.DataConsistencyError;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class DataConsistencyCheckJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job dataConsistencyCheckJob(
            Step staleWaitItemStep,
            Step prStatusMismatchStep) {
        return new JobBuilder("dataConsistencyCheckJob", jobRepository)
                .start(staleWaitItemStep)
                .next(prStatusMismatchStep)
                .build();
    }

    @Bean
    public Step staleWaitItemStep(
            ItemReader<ProductionRequestItem> staleWaitItemReader,
            ItemProcessor<ProductionRequestItem, DataConsistencyError> staleWaitItemProcessor,
            ItemWriter<DataConsistencyError> consistencyErrorWriter) {
        return new StepBuilder("staleWaitItemStep", jobRepository)
                .<ProductionRequestItem, DataConsistencyError>chunk(100, transactionManager)
                .reader(staleWaitItemReader)
                .processor(staleWaitItemProcessor)
                .writer(consistencyErrorWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(20)
                .build();
    }

    @Bean
    public Step prStatusMismatchStep(
            ItemReader<ProductionRequest> prReader,
            ItemProcessor<ProductionRequest, DataConsistencyError> prStatusMismatchProcessor,
            ItemWriter<DataConsistencyError> consistencyErrorWriter) {
        return new StepBuilder("prStatusMismatchStep", jobRepository)
                .<ProductionRequest, DataConsistencyError>chunk(100, transactionManager)
                .reader(prReader)
                .processor(prStatusMismatchProcessor)
                .writer(consistencyErrorWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(20)
                .build();
    }
}