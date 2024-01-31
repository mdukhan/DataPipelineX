package org.example.consumers;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.entities.Movie;
import org.example.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka message consumer for movie data.
 */
@Component
@Slf4j
public class MovieConsumer {

    MovieService movieService;

    private final KafkaTemplate<Integer, String> kafkaTemplate;

    public MovieConsumer(MovieService movieService,KafkaTemplate<Integer,String> kafkaTemplate) {
        this.movieService = movieService;
        this.kafkaTemplate=kafkaTemplate;
    }

    /**
     * Listens for messages on the "movies" topic and processes them using the MovieService.
     *
     * @param consumerRecord The Kafka ConsumerRecord containing movie data.
     * @throws JsonProcessingException If there's an issue processing JSON data.
     */
    @KafkaListener(topics = {"movies"})
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        try {
            log.info("ConsumerRecord : {}", consumerRecord);
            movieService.processMovie(consumerRecord);
        } catch (Exception e) {
            // Log the exception and send it to the error topic
            log.error(e.getMessage());
            kafkaTemplate.send("error-topic", e.getMessage());
        }
    }
}
