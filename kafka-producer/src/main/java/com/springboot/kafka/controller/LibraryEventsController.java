package com.springboot.kafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.kafka.dto.LibraryEvent;
import com.springboot.kafka.producer.LibraryEventsProducer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
}
