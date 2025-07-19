package com.springboot.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
public class LibraryEventsConsumerManualOffsetManagement implements AcknowledgingMessageListener<Integer, String> {

    @Override
    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(ConsumerRecord<Integer, String> data, Acknowledgment acknowledgment) {
        log.info("Received message: {}", data);

        Integer key = data.key();
        String value = data.value();

        log.info("Key: {}, Value: {}", key, value);

        acknowledgment.acknowledge();;
    }
}
