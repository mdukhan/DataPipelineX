package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.entities.Movie;
import org.example.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for processing movie data from Kafka messages.
 */
@Service
@Slf4j
public class MovieService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MovieRepository movieRepository;

    /**
     * Processes a movie received from Kafka.
     *
     * @param consumerRecord The Kafka ConsumerRecord containing movie data.
     * @throws JsonProcessingException If there's an issue processing JSON data.
     */
    public void processMovie(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        Movie movie = objectMapper.readValue(consumerRecord.value(), Movie.class);
        log.info("Movie : {} ", movie);
        save(movie);
    }

    /**
     * Saves a movie to the repository.
     *
     * @param movie The movie to be saved.
     */
    private void save(Movie movie) {
        movieRepository.save(movie);
        log.info("successfully persisted the movie {} ", movie);
    }
}
