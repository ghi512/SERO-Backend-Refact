package com.werp.sero.system.batch.processor;

import com.werp.sero.production.command.domain.aggregate.ProductionRequestItem;
import com.werp.sero.system.command.domain.aggregate.DataConsistencyError;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class StaleWaitItemProcessor
        implements ItemProcessor<ProductionRequestItem, DataConsistencyError> {

    @Override
    public DataConsistencyError process(ProductionRequestItem item) {
        return DataConsistencyError.builder()
                .domainType("PRODUCTION_REQUEST_ITEM")
                .targetId(item.getId())
                .errorCode("PIS_WAIT_STALE")
                .errorMessage("PR_ITEM(id=" + item.getId() + ")이 PIS_WAIT 상태로 3일 이상 방치됨")
                .detectedAt(LocalDateTime.now())
                .resolved(false)
                .build();
    }
}