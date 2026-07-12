package com.werp.sero.system.batch.config;

import com.werp.sero.production.command.domain.aggregate.ProductionRequest;
import com.werp.sero.production.command.domain.aggregate.ProductionRequestItem;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class DataConsistencyReaderConfig {

    @Bean
    @StepScope
    public JpaPagingItemReader<ProductionRequestItem> staleWaitItemReader(
            EntityManagerFactory emf,
            @Value("#{jobParameters['businessDate']}") String businessDate
    ) {
        LocalDateTime threshold = LocalDate.parse(businessDate)
                .minusDays(3) // 방치 기준 3일
                .atStartOfDay();

        return new JpaPagingItemReaderBuilder<ProductionRequestItem>()
                .name("staleWaitItemReader")
                .entityManagerFactory(emf)
                .queryString(
                        "SELECT p FROM ProductionRequestItem p " +
                                "WHERE p.status = 'PIS_WAIT' " +
                                "AND p.updatedAt < :threshold " +
                                "ORDER BY p.id"
                )
                .parameterValues(Map.of("threshold", threshold))
                .pageSize(100)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<ProductionRequest> prReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<ProductionRequest>()
                .name("prReader")
                .entityManagerFactory(emf)
                .queryString(
                        "SELECT p FROM ProductionRequest p " +
                                "WHERE p.status NOT IN ('PR_DONE', 'PR_TMP') " +
                                "ORDER BY p.id"
                )
                .pageSize(100)
                .build();
    }
}
