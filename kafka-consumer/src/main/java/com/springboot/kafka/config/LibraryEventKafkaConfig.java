package com.springboot.kafka.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.List;

@Configuration
@EnableKafka
public class LibraryEventKafkaConfig {

    @Value("${topic.retry}")
    private String retryTopic;

    @Value("${topic.dlt}")
    private String dltTopic;

    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    public DeadLetterPublishingRecoverer recoverer() {
        return new DeadLetterPublishingRecoverer(kafkaTemplate,
                (r, e) -> {
                    if (e.getCause() instanceof IllegalArgumentException) {
                        return new TopicPartition(retryTopic, r.partition());
                    }
                    else {
                        return new TopicPartition(dltTopic, r.partition());
                    }
                });
    }

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
        var exceptionToRetry = List.of(IllegalArgumentException.class);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer(), backOff);

        errorHandler.setRetryListeners(
                (record, exception, deliveryAttempt) -> {
                    System.out.println("Retrying record: " + record + ", attempt: " + deliveryAttempt);
                }
        );
        exceptionToRetry.forEach(errorHandler::addNotRetryableExceptions);

        return errorHandler;
    }

}
