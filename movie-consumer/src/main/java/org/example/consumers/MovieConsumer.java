package org.example.consumers;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka message consumer for movie data.
 */
@Component
@Slf4j
public class MovieConsumer {

    MovieService movieService;

    public MovieConsumer(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Listens for messages on the "movies" topic and processes them using the MovieService.
     *
     * @param consumerRecord The Kafka ConsumerRecord containing movie data.
     * @throws JsonProcessingException If there's an issue processing JSON data.
     */
    @KafkaListener(topics = {"movies"})
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {

        log.info("ConsumerRecord : {}", consumerRecord);
        movieService.processMovie(consumerRecord);
    }
}
