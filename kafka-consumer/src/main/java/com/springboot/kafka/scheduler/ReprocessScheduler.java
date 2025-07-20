package com.springboot.kafka.scheduler;

import com.springboot.kafka.service.FailedRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReprocessScheduler {

    private final FailedRecordService failedRecordService;

    @Scheduled(fixedDelay = 10000)
    public void reprocessFailedRecords() {
        log.info("Starting reprocessing of failed records...");
        failedRecordService.getRecoverableRecords().forEach(failedRecord -> {
            try {
                log.info("Reprocessing record: {}", failedRecord);
            } catch (Exception e) {
                log.error("Error reprocessing record: {}", failedRecord, e);
            }
        });

    }
}
