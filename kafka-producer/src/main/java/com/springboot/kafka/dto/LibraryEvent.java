package com.springboot.kafka.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record LibraryEvent(
        Integer libraryEventId,
        LibraryEventType libraryEventType,
        @NotNull
        @Valid
        Book book
) {
}
