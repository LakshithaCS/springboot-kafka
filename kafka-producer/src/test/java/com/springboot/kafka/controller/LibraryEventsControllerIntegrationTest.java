package com.springboot.kafka.controller;

import com.springboot.kafka.dto.LibraryEvent;
import com.springboot.kafka.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = "library-events")
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap-servers=${spring.embedded.kafka.brokers}"})
class LibraryEventsControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testPostLibraryEvent() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<LibraryEvent> entity = new HttpEntity<>(TestUtil.newLibraryEventRecordWithLibraryEventId(), headers);

        // when
        ResponseEntity<LibraryEvent> response = restTemplate.exchange(
                "/v1/libraryevent",
                HttpMethod.POST,
                entity,
                LibraryEvent.class
        );

        // then
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());

    }

}