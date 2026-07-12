package com.werp.sero.system.batch.writer;

import com.werp.sero.system.command.domain.aggregate.DataConsistencyError;
import com.werp.sero.system.command.domain.repository.DataConsistencyErrorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsistencyErrorWriter implements ItemWriter<DataConsistencyError> {

    private final DataConsistencyErrorRepository repository;

    @Override
    public void write(Chunk<? extends DataConsistencyError> chunk) {
        repository.saveAll(chunk.getItems());
    }
}