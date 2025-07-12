package com.springboot.kafka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.kafka.dto.LibraryEvent;
import com.springboot.kafka.util.TestUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = "library-events")
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap-servers=${spring.embedded.kafka.brokers}"})
class LibraryEventsControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static EmbeddedKafkaBroker broker;

    @Autowired
    private ObjectMapper objectMapper;

    private Consumer<Integer, String> consumer;

    @BeforeEach
    void setUp(@Autowired EmbeddedKafkaBroker embeddedKafkaBroker) {
        broker = embeddedKafkaBroker;
        consumer = new DefaultKafkaConsumerFactory<>(
                getConfigs(),
                new IntegerDeserializer(),
                new StringDeserializer()
        ).createConsumer();
        broker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

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

        ConsumerRecords<Integer, String> records = KafkaTestUtils.getRecords(consumer);
        assert records.count() == 1;

        records.forEach(
                record -> {
                    try {
                        LibraryEvent libraryEvent = objectMapper.readValue(record.value(), LibraryEvent.class);
                        Assertions.assertEquals(TestUtil.newLibraryEventRecordWithLibraryEventId().libraryEventId(), libraryEvent.libraryEventId());
                        Assertions.assertEquals(TestUtil.newLibraryEventRecordWithLibraryEventId().book(), libraryEvent.book());
                    } catch (Exception e) {
                        Assertions.fail("Failed to parse LibraryEvent from record", e);
                    }
                }
        );

    }


    private Map<String, Object> getConfigs() {
        Map<String, Object> configs = new HashMap<>(
                KafkaTestUtils.consumerProps("group1", "true", broker)
        );
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return configs;
    }

}