package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.entities.Movie;
import org.example.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        movie.setId(consumerRecord.key());
        log.info("Movie : {} ", movie);

        switch (movie.getMovieType()) {
            case NEW:
                save(movie);
                break;
            case UPDATE:
                //validate the libraryevent
                validate(movie);
                save(movie);
                break;
            default:
                log.info("Invalid Movie Type");
        }
    }

    /**
     * validates if a movie.Id does exist in the databases, then replace(update) that movie.
     *
     * @param movie the movie to be added
     * @throws IllegalArgumentException if the movie.Id is not found.
     */
    private void validate(Movie movie) {
        if (movie.getId() == null) {
            throw new IllegalArgumentException("Movie Id is missing");
        }

        Optional<Movie> movieOptional = movieRepository.findById(movie.getId());
        if (!movieOptional.isPresent()) {
            throw new IllegalArgumentException("No movie in the database was found. Please insert firstly a movie!");
        }
        log.info("updating the movie... : {} ", movieOptional.get());
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
