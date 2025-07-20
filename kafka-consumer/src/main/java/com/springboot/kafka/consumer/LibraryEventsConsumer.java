package com.springboot.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.kafka.service.LibraryEventsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LibraryEventsConsumer {

    private final LibraryEventsService libraryEventsService;

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onLibraryEvent(ConsumerRecord<Integer, String> message) throws JsonProcessingException {
        log.info("Received message: {}", message);
        libraryEventsService.processLibraryEvent(message);
    }

    @KafkaListener(topics = "${topic.retry}", groupId = "${spring.kafka.consumer.group-id}")
    public void onLibraryRetryEvent(ConsumerRecord<Integer, String> message) {
        log.info("Received Retry message: {}", message);
    }

}
