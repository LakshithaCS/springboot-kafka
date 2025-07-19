package com.springboot.kafka.consumer;

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
    public void onLibraryEvent(ConsumerRecord<Integer, String> message) {

        log.info("Received message: {}", message);
        try {
            libraryEventsService.processLibraryEvent(message);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
        }

    }

}
