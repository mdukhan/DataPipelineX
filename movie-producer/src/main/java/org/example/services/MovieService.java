package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.producers.MovieProducer;
import org.example.producers.MovieRequestListener;
import org.example.records.Movie;
import org.example.records.MovieType;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MovieService {

    /**
     * Movie producer instance responsible for sending movie records to Kafka.
     */
    private final MovieProducer movieProducer;

    /**
     * Movie request listener instance responsible for processing movie requests received from Kafka.
     */
   private final MovieRequestListener movieRequestListener;

    /**
     * Constructs a MovieService with the specified movie producer and movie request listener.
     *
     * @param movieProducer       The movie producer instance.
     * @param movieRequestListener The movie request listener instance.
     */
    public MovieService(MovieProducer movieProducer, MovieRequestListener movieRequestListener) {
        this.movieProducer = movieProducer;
        this.movieRequestListener = movieRequestListener;
    }

    /**
     * Parses a CSV line and creates a Movie object.
     *
     * @param csvLine The CSV line to parse.
     * @return A Movie object created from the CSV data.
     * @throws IllegalArgumentException If the CSV line is invalid.
     */
    public Movie parseCsvLine(String csvLine) {
        String[] values = csvLine.split(",");
        if (values.length >= 3) {
            return new Movie(Integer.parseInt(values[0]), MovieType.NEW, values[1], values[2]);
        } else {
            // Handle invalid CSV lines appropriately
            throw new IllegalArgumentException("Invalid CSV line: " + csvLine);
        }
    }

    /**
     * Sends a movie to Kafka using the movie producer.
     *
     * @param movie The movie to send.
     * @return A CompletableFuture representing the result of the send operation.
     * @throws JsonProcessingException If there's an issue processing JSON data.
     */
    public CompletableFuture<SendResult<Integer, String>> sendMovie(Movie movie) throws JsonProcessingException {
        return movieProducer.sendMovieRecord(movie);
    }

    /**
     * Retrieves the list of movies from the movie request listener.
     *
     * @return The list of movies received from the consumer.
     */
   private List<Movie> getMovieListResponseFromConsumer(){
        return movieRequestListener.getReceivedMoviesList();
    }

    /**
     * Retrieves a JSON representation of the received movies.
     *
     * @return A JSON string representing the received movies.
     * @throws JsonProcessingException If there's an issue processing JSON data.
     */
    public String getJsonResponse() throws JsonProcessingException {
       return extractJsonFromMoviesList();
    }

    /**
     * Extracts a JSON representation from the list of received movies and clears the list.
     *
     * @return A JSON string representing the received movies.
     * @throws JsonProcessingException If there's an issue processing JSON data.
     */
    private String extractJsonFromMoviesList() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Movie> movies = getMovieListResponseFromConsumer();
        String jsonList = objectMapper.writeValueAsString(movies);
        clearListFromConsumer();
        return jsonList;
    }

    /**
     * Clears the list of received movies from the consumer.
     */
    private void clearListFromConsumer() {
        movieRequestListener.clearList();
    }
}
