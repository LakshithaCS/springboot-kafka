package com.springboot.kafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.kafka.dto.LibraryEvent;
import com.springboot.kafka.dto.LibraryEventType;
import com.springboot.kafka.producer.LibraryEventsProducer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@Slf4j
public class LibraryEventsController {

    private final LibraryEventsProducer libraryEventsProducer;

    public LibraryEventsController(LibraryEventsProducer libraryEventsProducer) {
        this.libraryEventsProducer = libraryEventsProducer;
    }

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {

        log.info("LibraryEvent: {}", libraryEvent);
        libraryEventsProducer.sendLibraryEventSynchronous(libraryEvent);

        return ResponseEntity.status(CREATED).body(libraryEvent);
    }

    @PutMapping("/v1/libraryevent")
    public ResponseEntity<?> putLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {
        ResponseEntity<String> response = validationRequest(libraryEvent);
        if (response != null) return response;

        log.info("LibraryEvent: {}", libraryEvent);
        libraryEventsProducer.sendLibraryEventSynchronous(libraryEvent);

        return ResponseEntity.status(CREATED).body(libraryEvent);
    }

    private ResponseEntity<String> validationRequest(LibraryEvent libraryEvent) {
        if (libraryEvent.libraryEventId() == null) {
            return ResponseEntity.badRequest().body("LibraryEvent ID cannot be null for update operation");
        }

        if (!libraryEvent.libraryEventType().equals(LibraryEventType.UPDATE)) {
            return ResponseEntity.badRequest().body("LibraryEvent type must be UPDATE for update operation");
        }
        return null;
    }
}
