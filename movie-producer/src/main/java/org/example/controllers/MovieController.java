package org.example.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.records.Movie;
import org.example.records.MovieType;
import org.example.services.MovieService;
import org.example.services.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * REST controller class for handling movie-related requests.
 */
@RestController
@Slf4j
public class MovieController {

    private final MovieService movieService;

    private RatingService ratingService;

    /**
     * Constructor for MovieController.
     *
     * @param movieService The MovieService to be injected.
     */
    public MovieController(MovieService movieService,RatingService ratingService) {
        this.movieService = movieService;
        this.ratingService=ratingService;
    }


    /**
     * Endpoint for uploading movies from a CSV file.
     *
     * @param csvFile The CSV file containing movie data.
     * @return ResponseEntity with the result of the file upload operation.
     */
    @PostMapping("/api/upload-csv-file/movies")
    public ResponseEntity<String> postMoviesFromCSV(@RequestParam("file") MultipartFile csvFile) {
        if (csvFile.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                movieService.sendMovie(movieService.parseMoviesCsvLine(line));
            }

            return ResponseEntity.status(HttpStatus.CREATED).body("File processed successfully");
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file");
        }
    }

    @PostMapping("/api/upload-csv-file/ratings")
    public ResponseEntity<String> postRatingsFromCSV(@RequestParam("file") MultipartFile csvFile) {
        if (csvFile.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ratingService.sendRatingRecord(ratingService.parseRatingCsvLine(line));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("File processed successfully");
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file");
        }
    }


    /**
     * Endpoint for adding a movie.
     *
     * @param movie The Movie object to be added.
     * @return ResponseEntity with the result of the movie addition operation.
     * @throws JsonProcessingException if there is an issue processing JSON.
     */
    @PostMapping("/add/movie")
    public ResponseEntity<?> addMovie(@RequestBody @Valid Movie movie)
            throws JsonProcessingException {

        if (movie.Id() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the MovieId");
        }
        if (!MovieType.NEW.equals(movie.movieType())) {
            Movie movieWithNewType = new Movie(movie.Id(), MovieType.NEW, movie.title(), movie.genres());
            movieService.sendMovie(movieWithNewType);
            return ResponseEntity.status(HttpStatus.CREATED).body(movieWithNewType);
        } else {
            movieService.sendMovie(movie);
            return ResponseEntity.status(HttpStatus.CREATED).body(movie);
        }
    }

    /**
     * Endpoint for updating a movie.
     *
     * @param movie The Movie object to be added.
     * @return ResponseEntity with the result of the movie addition operation.
     * @throws JsonProcessingException if there is an issue processing JSON.
     */
    @PutMapping("/update/movie")
    public ResponseEntity<?> updateMovie(@RequestBody @Valid Movie movie) throws JsonProcessingException {

        if (movie.Id() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the MovieId");
        }

        if (!MovieType.UPDATE.equals(movie.movieType())) {
            Movie movieWithUpdate = new Movie(movie.Id(), MovieType.UPDATE, movie.title(), movie.genres());
            movieService.sendMovie(movieWithUpdate);
            return ResponseEntity.status(HttpStatus.OK).body(movieWithUpdate);
        } else {
            movieService.sendMovie(movie);
            return ResponseEntity.status(HttpStatus.OK).body(movie);
        }
    }

    /**
     * Deletes a movie based on the provided Movie object.
     *
     * @param movie The Movie object containing details of the movie to be deleted.
     * @return ResponseEntity containing the deleted Movie object or an error message.
     * @throws JsonProcessingException If there is an issue processing JSON.
     */
    @DeleteMapping("/delete/movie")
    public ResponseEntity<?> deleteMovie(@RequestBody @Valid Movie movie) throws JsonProcessingException {
        if (movie.Id() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the MovieId");
        }

        if (!MovieType.DELETE.equals(movie.movieType())) {
            Movie movieWithDELETE = new Movie(movie.Id(), MovieType.DELETE, movie.title(), movie.genres());
            movieService.sendMovie(movieWithDELETE);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(movieWithDELETE);
        } else {
            movieService.sendMovie(movie);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(movie);
        }
    }

    /**
     * Retrieves movies based on specified genres.
     *
     * @param genres The genres to search for.
     * @return ResponseEntity containing a JSON list of movies matching the given genres.
     * @throws JsonProcessingException If there is an issue processing JSON.
     * @throws InterruptedException    If the thread is interrupted while sleeping.
     */
    @GetMapping("/get/movies/genres/{genres}")
    public ResponseEntity<?> searchMoviesByGenres(@PathVariable String genres) throws JsonProcessingException, InterruptedException {
        final int movieIdForSearchingGenres = -2;
        Movie movieGetRequestByGenres = new Movie(movieIdForSearchingGenres, MovieType.GET, "", genres);
        movieService.sendMovie(movieGetRequestByGenres);
        Thread.sleep(300);
        String jsonMoviesResponseList = movieService.getJsonResponse();
        return ResponseEntity.status(HttpStatus.OK).body(jsonMoviesResponseList);
    }

    /**
     * Retrieves movies based on specified title.
     *
     * @param title The title to search for.
     * @return ResponseEntity containing a JSON list of movies matching the given title.
     * @throws JsonProcessingException If there is an issue processing JSON.
     * @throws InterruptedException    If the thread is interrupted while sleeping.
     */
    @GetMapping("/get/movies/title/{title}")
    public ResponseEntity<?> searchMoviesByTitle(@PathVariable String title) throws JsonProcessingException, InterruptedException {
        final int movieIdForSearchingTitle = -1;
        Movie movieGetRequestByTitle = new Movie(movieIdForSearchingTitle, MovieType.GET, title, ""); // Create a Movie object with necessary details
        movieService.sendMovie(movieGetRequestByTitle);
        Thread.sleep(300);
        String jsonMoviesResponseList = movieService.getJsonResponse();
        return ResponseEntity.status(HttpStatus.OK).body(jsonMoviesResponseList);
    }
}
