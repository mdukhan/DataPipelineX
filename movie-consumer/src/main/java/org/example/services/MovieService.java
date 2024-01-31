package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.entities.Movie;
import org.example.repositories.MovieRepository;
import org.example.producers.MovieGetResponseProducer;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for processing movie data from Kafka messages.
 */
@Service
@Slf4j
public class MovieService {

    /**
     * Constant representing the search criteria for movies by title.
     */

    final int searchMovieByTitle = -1;

    /**
     * Constant representing the search criteria for movies by genres.
     */
    final int searchMovieByGenres = -2;

    /**
     * Object mapper for JSON serialization and deserialization.
     */
    ObjectMapper objectMapper;

    /**
     * Producer for sending movie response messages.
     */
    private MovieGetResponseProducer movieGetResponseProducer;

    /**
     * Repository for interacting with the movie data storage.
     */
    private MovieRepository movieRepository;

    /**
     * Constructs a MovieService with the provided dependencies.
     *
     * @param objectMapper          The object mapper for JSON serialization and deserialization.
     * @param movieGetResponseProducer The producer for sending movie response messages.
     * @param movieRepository       The repository for interacting with the movie data storage.
     */
    public MovieService(ObjectMapper objectMapper, MovieGetResponseProducer movieGetResponseProducer, MovieRepository movieRepository) {
        this.objectMapper = objectMapper;
        this.movieGetResponseProducer = movieGetResponseProducer;
        this.movieRepository = movieRepository;
    }

    /**
     * Processes a movie received from Kafka, handling different movie types.
     *
     * @param consumerRecord The Kafka ConsumerRecord containing movie data.
     * @throws JsonProcessingException If there's an issue processing JSON data.
     */
    public void processMovie(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        // Deserialize the JSON string into a Movie object
        Movie movie = objectMapper.readValue(consumerRecord.value(), Movie.class);
        // Set the ID from Kafka record key
        movie.setId(consumerRecord.key());
        log.debug("Received Movie: {} ", movie);

        // Handle different Movie types
        switch (movie.getMovieType()) {
            case NEW:
                // Save the movie if the ID does not exist
                saveIfIdNotExists(movie);
                break;
            case UPDATE:
                // Validate the movie and update it
                validate(movie);
                log.info("Updating the movie: {} ", movie);
                save(movie);
                break;
            case DELETE:
                // Validate the movie and delete it
                validate(movie);
                delete(movie);
                break;
            case GET:
                // Handle the GET type with different search criteria
                handleGetRequest(movie);
                break;
            default:
                log.error("Invalid Movie Type");
        }
    }

    /**
     * Handles the GET type of Movie, performing searches based on title or genres.
     *
     * @param movie The Movie object with GET type.
     * @throws JsonProcessingException If there's an issue processing JSON data.
     */
    private void handleGetRequest(Movie movie) throws JsonProcessingException {
        if (movie.getId() == searchMovieByTitle) {
            // Search movies by title and send found entities to response topic
            handleSearchByTitle(movie);
        } else if (movie.getId() == searchMovieByGenres) {
            // Search movies by genres and send found entities to response topic
            handleSearchByGenres(movie);
        }
    }

    /**
     * Searches movies by title and sends found entities to the response topic.
     *
     * @param movie The Movie object with GET type and search by title.
     * @throws JsonProcessingException If there's an issue processing JSON data.
     */
    private void handleSearchByTitle(Movie movie) throws JsonProcessingException {
        for (Movie foundMovieFromTitle : movieRepository.findByTitleContainingIgnoreCase(movie.getTitle())) {
            movieGetResponseProducer.sendFoundEntity(foundMovieFromTitle);
        }
    }

    /**
     * Searches movies by genres and sends found entities to the response topic.
     *
     * @param movie The Movie object with GET type and search by genres.
     * @throws JsonProcessingException If there's an issue processing JSON data.
     */
    private void handleSearchByGenres(Movie movie) throws JsonProcessingException {
        for (Movie foundMovieFromGenres : movieRepository.findByGenresContainingIgnoreCase(movie.getGenres())) {
            movieGetResponseProducer.sendFoundEntity(foundMovieFromGenres);
        }
    }

    private void delete(Movie movie) {
        movieRepository.delete(movie);
        log.info("successfully deleted the movie {} ", movie);
    }

    /**
     * validates if a movie.Id does exist in the databases, then replace(update) that movie.
     *
     * @param movie the movie to be added
     * @throws IllegalArgumentException if the movie.Id is not found.
     */
    private Optional<Movie> validate(Movie movie) {
        if (movie.getId() == null) {
            throw new IllegalArgumentException("Movie Id is missing");
        }

        Optional<Movie> movieOptional = movieRepository.findById(movie.getId());
        if (!movieOptional.isPresent()) {
            throw new IllegalArgumentException("No movie in the database was found. Please insert firstly a movie!");
        }
        return movieOptional;
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

    /**
     * Save the movie only if the ID does not already exist.
     *
     * @param movie The movie to be saved.
     */
    private void saveIfIdNotExists(Movie movie) {
        Optional<Movie> existingMovie = movieRepository.findById(movie.getId());
        if (existingMovie.isPresent()) {
            log.info("Movie with ID {} already exists. Skipping save.", movie.getId());
            log.debug("movieRepository.count {}", movieRepository.count());

            movie.setId((int) (movieRepository.count() + 1));
            movieRepository.save(movie);
        } else {
            save(movie);
        }
    }
}
