package org.example.controllers;


import lombok.extern.slf4j.Slf4j;
import org.example.records.Movie;
import org.example.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
public class MovieController {


    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }


    @PostMapping("/api/upload-csv-file-1")
    public ResponseEntity<String> uploadCsvFile1(@RequestParam("file") MultipartFile csvFile) {
        if (csvFile.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {
            // Pass the MultipartFile to the MovieService for processing
            List<Movie> movieList = movieService.getMovieListFromFile(csvFile);
            for (Movie movie : movieList) {
                log.info(movie.toString());
                movieService.sendMoviebyId(movieList, movie.Id());
            }


            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
            return ResponseEntity.status(500).body("Error processing the file");
        }
    }


}
