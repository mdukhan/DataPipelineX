package org.example.producers;

import lombok.extern.slf4j.Slf4j;
import org.example.records.Movie;
import org.example.records.MovieType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class MovieRequestListener {

    // List to store received movies
    private List<Movie> receivedMoviesList = new ArrayList<>();

    // Variable to store the latest received movie
    private Movie receivedMovie;

    /**
     * Kafka listener method to process movie responses from the GET requests.
     *
     * @param movie The raw movie information received from Kafka.
     */
    @KafkaListener(topics = "movie-response-topic")
    public void processMovieRequest(String movie) {
        // Extract and save movie information from the received raw data
        Movie movieInfo = extractAndSaveMovieInfo(movie);

        // Check if extraction was successful
        if (movieInfo != null) {
            log.info("Received Movie: title - {}, genres - {}", movieInfo.title(), movieInfo.genres());
        }
    }

    /**
     * Extracts and saves movie information from the raw movie data.
     *
     * @param movie The raw movie information.
     * @return The Movie object with extracted data, or null if extraction fails.
     */
    private Movie extractAndSaveMovieInfo(String movie) {
        Pattern pattern = Pattern.compile("title=([^,]+), genres=([^)]+)");
        Matcher matcher = pattern.matcher(movie);

        // Check if the pattern is found
        if (matcher.find()) {
            // Group 1 of the match contains the captured title
            // Group 2 of the match contains the captured genres
            receivedMovie = new Movie(0, MovieType.GET, matcher.group(1).trim(), matcher.group(2).trim());
            setReceivedMovie(receivedMovie);
            addReceivedMovieToList();
            return getReceivedMovie();
        }
        return null;
    }

    /**
     * Gets the list of received movies.
     *
     * @return The list of received movies.
     */
    public List<Movie> getReceivedMoviesList() {
        return this.receivedMoviesList;
    }

    /**
     * Gets the latest received movie.
     *
     * @return The latest received movie.
     */
    public Movie getReceivedMovie() {
        return this.receivedMovie;
    }

    /**
     * Adds the latest received movie to the list.
     */
    public void addReceivedMovieToList() {
        this.receivedMoviesList.add(getReceivedMovie());
    }

    /**
     * Sets the latest received movie.
     *
     * @param receivedMovie The latest received movie.
     */
    public void setReceivedMovie(Movie receivedMovie) {
        this.receivedMovie = receivedMovie;
    }

    /**
     * Clears the list of received movies.
     */
    public void clearList() {
        this.receivedMoviesList.clear();
    }
}
