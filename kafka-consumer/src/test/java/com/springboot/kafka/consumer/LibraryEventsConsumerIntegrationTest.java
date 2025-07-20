package com.springboot.kafka.consumer;


import com.springboot.kafka.entity.LibraryEvent;
import com.springboot.kafka.repository.LibraryEventsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.SECONDS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = "library-events")
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}"})
class LibraryEventsConsumerIntegrationTest {

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private LibraryEventsRepository libraryEventsRepository;

    @BeforeEach
    public void setUp() {
        libraryEventsRepository.deleteAll();
    }

    @Test
    public void testPublishNewLibraryEvent() throws ExecutionException, InterruptedException {
        // Given
        String libraryEvent = "{" +
                "  \"libraryEventType\": \"NEW\"," +
                "  \"book\": {" +
                "    \"bookId\": 123," +
                "    \"bookName\": \"Kafka Using Spring Boot\"," +
                "    \"bookAuthor\": \"Dilip\"" +
                "  }\n" +
                "}";
        kafkaTemplate.send("library-events", libraryEvent).get();

        // When
        new CountDownLatch(1).await(3, SECONDS);

        // Then
        Assertions.assertEquals(1, libraryEventsRepository.count());
    }

    @Test
    public void testPublishUpdateLibraryEvent() throws ExecutionException, InterruptedException {
        // Given
        String libraryNewEvent = "{" +
                "  \"libraryEventType\": \"NEW\"," +
                "  \"book\": {" +
                "    \"bookId\": 123," +
                "    \"bookName\": \"Kafka Using Spring Boot\"," +
                "    \"bookAuthor\": \"Dilip\"" +
                "  }\n" +
                "}";
        kafkaTemplate.send("library-events", libraryNewEvent).get();

        String libraryUpdateEvent = "{" +
                "  \"libraryEventId\": 1," +
                "  \"libraryEventType\": \"UPDATE\"," +
                "  \"book\": {" +
                "    \"bookId\": 123," +
                "    \"bookName\": \"Kafka Using Spring Boot 2\"," +
                "    \"bookAuthor\": \"Dilip\"" +
                "  }" +
                "}";
        kafkaTemplate.send("library-events", libraryUpdateEvent).get();

        // When
        new CountDownLatch(1).await(3, SECONDS);

        // Then
        Assertions.assertEquals(1, libraryEventsRepository.count());
        LibraryEvent event = libraryEventsRepository.findAll().iterator().next();
        Assertions.assertEquals("Kafka Using Spring Boot 2", event.getBook().getBookName());
    }
}