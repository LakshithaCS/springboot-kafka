package com.springboot.kafka.repository;

import com.springboot.kafka.entity.FailedRecord;
import com.springboot.kafka.entity.RecordType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailedRecordRepository extends CrudRepository<FailedRecord, Integer> {
    List<FailedRecord> findAllByRecordType(RecordType recordType);
}
