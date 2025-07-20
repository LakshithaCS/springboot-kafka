package com.springboot.kafka.service;

import com.springboot.kafka.entity.FailedRecord;
import com.springboot.kafka.entity.RecordType;
import com.springboot.kafka.repository.FailedRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FailedRecordService {

    private final FailedRecordRepository failedRecordRepository;

    public void saveFailedRecord(String recordValue, RecordType recordType) {

        FailedRecord failedRecord = new FailedRecord();
        failedRecord.setRecordType(recordType);
        failedRecord.setRecordValue(recordValue);

        failedRecordRepository.save(failedRecord);
    }

    public List<FailedRecord> getRecoverableRecords() {
        return failedRecordRepository.findAllByRecordType(RecordType.RECOVERABLE);
    }
}
