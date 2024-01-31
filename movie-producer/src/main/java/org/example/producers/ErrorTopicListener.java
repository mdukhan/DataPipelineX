package org.example.producers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka listener service that handles error messages from the "error-topic".
 * Logs the received error messages.
 */
@Service
@Slf4j
public class ErrorTopicListener {

    /**
     * Listens for error messages from the "error-topic" Kafka topic.
     * Handles the errors by logging them.
     *
     * @param errorMessage The error message received from the Kafka topic.
     */
    @KafkaListener(topics = "error-topic")
    public void handleErrors(String errorMessage) {
        log.error("Received error message: " + errorMessage);
    }
}

