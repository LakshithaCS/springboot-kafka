package com.springboot.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.kafka.dto.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class LibraryEventsProducer {

    @Value("${spring.kafka.topic}")
    private String topicName;

    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public LibraryEventsProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {

        int key = libraryEvent.libraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        log.info("LibraryEvent key: {}, value: {}", key, value);

        CompletableFuture<SendResult<Integer, String>> completableFuture =  kafkaTemplate.send(
                topicName,
                key,
                value
        );

        completableFuture.whenComplete(
                (sendResult, throwable) -> {
                    if (throwable != null) {
                        log.error("Error sending message: {} ", throwable.getMessage());
                    } else {
                        log.info("Message sent successfully: {} ", sendResult.getProducerRecord().value());
                    }
                }
        );
    }

    public void sendLibraryEventSynchronous(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {

        int key = libraryEvent.libraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        log.info("LibraryEvent key: {}, value: {}", key, value);
        SendResult<Integer, String> result =  kafkaTemplate.send(
                topicName,
                key,
                value
        ).get();
        log.info("Message sent successfully: {} ", result.getProducerRecord().value());
    }

}
