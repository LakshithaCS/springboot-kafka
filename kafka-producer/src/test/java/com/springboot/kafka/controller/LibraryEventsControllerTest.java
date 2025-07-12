package com.springboot.kafka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.kafka.producer.LibraryEventsProducer;
import com.springboot.kafka.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryEventsController.class)
@Import(LibraryEventsControllerTest.TestConfig.class)
class LibraryEventsControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final LibraryEventsProducer libraryEventsProducer;

    @Autowired
    LibraryEventsControllerTest(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            LibraryEventsProducer libraryEventsProducer // injected from @TestConfiguration
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.libraryEventsProducer = libraryEventsProducer;
    }

    @Test
    public void testPostLibraryEvent() throws Exception {
        // given
        String payload = objectMapper.writeValueAsString(TestUtil.newLibraryEventRecordWithLibraryEventId());
        doNothing().when(libraryEventsProducer).sendLibraryEvent(any());

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/libraryevent")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isCreated());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public LibraryEventsProducer libraryEventsProducer() {
            return mock(LibraryEventsProducer.class);
        }
    }

}