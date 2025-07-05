package com.springboot.kafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.kafka.dto.LibraryEvent;
import com.springboot.kafka.producer.LibraryEventsProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@Slf4j
public class LibraryEventsController {

    private final LibraryEventsProducer libraryEventsProducer;

    public LibraryEventsController(LibraryEventsProducer libraryEventsProducer) {
        this.libraryEventsProducer = libraryEventsProducer;
    }

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {

        log.info("LibraryEvent: {}", libraryEvent);
        libraryEventsProducer.sendLibraryEvent(libraryEvent);

        return ResponseEntity.status(CREATED).body(libraryEvent);
    }
}
