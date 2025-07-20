package com.springboot.kafka.config;

import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.List;

@Configuration
@EnableKafka
public class LibraryEventKafkaConfig {

    @Bean
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);
        factory.setConcurrency(3); // Set the concurrency level for parallel processing
        factory.setCommonErrorHandler(getErrorHandler()); // Set a common error handler if needed
        // factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    private CommonErrorHandler getErrorHandler() {
        FixedBackOff backOff = new FixedBackOff(1000L, 3); // Retry 3 times with a 1 second interval
        var exceptionToIgnore = List.of(IllegalArgumentException.class);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(backOff);

        errorHandler.setRetryListeners(
                (record, exception, deliveryAttempt) -> {
                    System.out.println("Retrying record: " + record + ", attempt: " + deliveryAttempt);
                }
        );
        exceptionToIgnore.forEach(errorHandler::addRetryableExceptions);

        return errorHandler;
    }

}
