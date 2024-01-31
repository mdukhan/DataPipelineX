package org.example.producers;

import lombok.extern.slf4j.Slf4j;
import org.example.entities.Movie;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Component responsible for producing (sending) responses to movie search requests.
 */
@Component
@Slf4j
public class MovieGetResponseProducer {

    /**
     * KafkaTemplate for sending messages to Kafka topics.
     */
    private final KafkaTemplate<Integer, String> kafkaTemplate;

    /**
     * Constructs a MovieGetResponseProducer with the provided KafkaTemplate.
     *
     * @param kafkaTemplate The KafkaTemplate for sending messages to Kafka topics.
     */
    public MovieGetResponseProducer(KafkaTemplate<Integer, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends a movie entity to the "movie-response-topic" after a successful search.
     * If an exception occurs during the process, logs the exception and sends it to the "error-topic".
     *
     * @param movie The Movie entity found in the database.
     */
    public void sendFoundEntity(Movie movie) {
        try {
            log.debug("Found the movie entity in the database!");
            kafkaTemplate.send("movie-response-topic", movie.toString());
        } catch (Exception e) {
            // Log the exception and send it to the error topic
            log.error(e.getMessage());
            kafkaTemplate.send("error-topic", e.getMessage());
        }
    }
}

