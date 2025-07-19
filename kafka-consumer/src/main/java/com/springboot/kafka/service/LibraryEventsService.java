package com.springboot.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.kafka.entity.LibraryEvent;
import com.springboot.kafka.repository.LibraryEventsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LibraryEventsService {

    private final LibraryEventsRepository libraryEventsRepository;
    private final ObjectMapper objectMapper;

    public void processLibraryEvent(ConsumerRecord<Integer, String> record) throws JsonProcessingException {

        String value = record.value();
        LibraryEvent libraryEvent = objectMapper.readValue(value, LibraryEvent.class);

        switch (libraryEvent.getLibraryEventType()) {
            case NEW:
                log.info("Processing new library event: {}", libraryEvent);
                saveLibraryEvent(libraryEvent);
                break;
            case UPDATE:
                updateLibraryEvent(libraryEvent);
                log.info("Processing update library event: {}", libraryEvent);
                break;
            default:
                log.error("Unknown library event type: {}", libraryEvent.getLibraryEventType());
                throw new IllegalArgumentException("Unknown library event type");
        }
    }

    private void updateLibraryEvent(LibraryEvent libraryEvent) {
        if (libraryEvent.getLibraryEventId() == null) {
            log.error("LibraryEvent ID cannot be null for update operation");
            throw new IllegalArgumentException("LibraryEvent ID cannot be null for update operation");
        }

        LibraryEvent existingLibraryEvent = libraryEventsRepository.findById(libraryEvent.getLibraryEventId())
                .orElseThrow(() -> new IllegalArgumentException("LibraryEvent not found for ID: " + libraryEvent.getLibraryEventId()));

        existingLibraryEvent.setBook(libraryEvent.getBook());
        existingLibraryEvent.setLibraryEventType(libraryEvent.getLibraryEventType());
        saveLibraryEvent(existingLibraryEvent);
    }

    private void saveLibraryEvent(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        libraryEventsRepository.save(libraryEvent);
        log.info("Library event saved: {}", libraryEvent);
    }

}
