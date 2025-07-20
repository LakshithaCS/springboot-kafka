package com.springboot.kafka.repository;

import com.springboot.kafka.entity.FailedRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedRecordRepository extends CrudRepository<FailedRecord, Integer> {

}
