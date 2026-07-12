package com.werp.sero.system.batch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/system/batch")
@RequiredArgsConstructor
public class BatchTestController {

    private final JobLauncher jobLauncher;
    private final Job dataConsistencyCheckJob;

    @PostMapping("/consistency-check")
    public String run() throws Exception {
        jobLauncher.run(dataConsistencyCheckJob,
                new JobParametersBuilder()
                        .addString("businessDate", LocalDate.now().toString())
                        .addLong("requestedAt", System.currentTimeMillis())  // 매번 새 파라미터 조합 만들기 위함 (아래 설명)
                        .toJobParameters());
        return "실행 요청 완료. 로그와 DB(data_consistency_error) 확인하세요.";
    }
}