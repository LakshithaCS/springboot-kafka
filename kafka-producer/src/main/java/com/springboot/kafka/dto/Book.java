package com.springboot.kafka.dto;

public record Book(
        Integer bookId,
        String bookName,
        String bookAuthor
) {
}
