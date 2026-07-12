package com.werp.sero.system.batch.processor;

import com.werp.sero.production.command.domain.aggregate.ProductionRequest;
import com.werp.sero.production.command.domain.aggregate.ProductionRequestItem;
import com.werp.sero.production.command.domain.repository.PRItemRepository;
import com.werp.sero.system.command.domain.aggregate.DataConsistencyError;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PRStatusMismatchProcessor
        implements ItemProcessor<ProductionRequest, DataConsistencyError> {

    private final PRItemRepository prItemRepository;

    @Override
    public DataConsistencyError process(ProductionRequest pr) {
        List<ProductionRequestItem> items =
                prItemRepository.findAllByProductionRequest_Id(pr.getId());

        if (items.isEmpty()) return null;

        boolean allDone = items.stream()
                .allMatch(i -> "PIS_DONE".equals(i.getStatus()));

        if (allDone && !"PR_DONE".equals(pr.getStatus())) {
            return DataConsistencyError.builder()
                    .domainType("PRODUCTION_REQUEST")
                    .targetId(pr.getId())
                    .errorCode("PR_STATUS_MISMATCH")
                    .errorMessage(String.format(
                            "PR(id=%d)의 모든 PR_ITEM이 완료됐는데 PR 상태가 %s로 남아있음",
                            pr.getId(), pr.getStatus()))
                    .detectedAt(LocalDateTime.now())
                    .resolved(false)
                    .build();
        }
        return null;
    }
}