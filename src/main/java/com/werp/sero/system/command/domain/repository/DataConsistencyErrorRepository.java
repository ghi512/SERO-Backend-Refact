package com.werp.sero.system.command.domain.repository;

import com.werp.sero.system.command.domain.aggregate.DataConsistencyError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataConsistencyErrorRepository extends JpaRepository<DataConsistencyError, Long> {
}
