package org.example.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.example.records.Movie;
import org.example.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

/**
 * REST controller class for handling movie-related requests.
 */
@RestController
@Slf4j
public class MovieController {


    private final MovieService movieService;

    /**
     * Constructor for MovieController.
     *
     * @param movieService The MovieService to be injected.
     */
    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }


    /**
     * Endpoint for uploading movies from a CSV file.
     *
     * @param csvFile The CSV file containing movie data.
     * @return ResponseEntity with the result of the file upload operation.
     */
    @PostMapping("/api/upload-csv-file")
    public ResponseEntity<String> postMoviesFromCSV(@RequestParam("file") MultipartFile csvFile) {
        if (csvFile.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }
        try {
            // Pass the MultipartFile to the MovieService for processing
            List<Movie> movieList = movieService.getMovieListFromFile(csvFile);
            for (Movie movie : movieList) {
                movieService.sendMoviebyId(movieList, movie.Id());
                log.info("Message sent successfully {}", ResponseEntity.status(HttpStatus.CREATED).body(movie));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(movieList.toString());
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
            return ResponseEntity.status(500).body("Error processing the file");
        }
    }

    /**
     * Endpoint for adding a movie.
     *
     * @param movie The Movie object to be added.
     * @return ResponseEntity with the result of the movie addition operation.
     * @throws JsonProcessingException if there is an issue processing JSON.
     */
    @PostMapping("/v1/movie")
    public ResponseEntity<?> postMovie(@RequestBody @Valid Movie movie)
            throws JsonProcessingException {

        if (movie.Id() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the MovieId");
        }

        movieService.sendMovie(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(movie);
    }
}
