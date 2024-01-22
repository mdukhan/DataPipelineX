package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.example.producers.MovieProducer;
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

    private final MovieProducer movieProducer;

    public MovieService(MovieProducer movieProducer) {
        this.movieProducer = movieProducer;
    }

    public Movie getMovie(List<Movie> movieRecordsList, int movieId) {
        for (Movie movieRecord : movieRecordsList) {
            if (movieId == movieRecord.Id()) {
                log.info(String.format("Es wurde der Film: %s ausgewählt.", movieRecord.title()));
                log.info(String.format(movieRecord.movieType().toString()));
                log.info(String.format(movieRecord.genres().contains("|") ? "Genres: %s" : "Genre: %s", movieRecord.genres()));
                log.info(movieRecord.toString());
                return movieRecord;
            }
        }
        return null;
    }

    public List<Movie> getMovieListFromPath(final String filePath) throws FileNotFoundException {
        List<Movie> movieList = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(",");
                movieList.add(new Movie(Integer.parseInt(values[0]), MovieType.NEW, values[1], values[2]));
            }
        } catch (IOException e) {
            throw new FileNotFoundException(filePath);
        }
        return movieList;
    }

    public List<Movie> getMovieListFromFile(MultipartFile file) throws IOException {
        List<Movie> movieList = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(",");
                movieList.add(new Movie(Integer.parseInt(values[0]), MovieType.NEW, values[1], values[2]));
            }
        }
        return movieList;
    }


    public void sendMovieById(List<Movie> movieList, int Id) throws FileNotFoundException, JsonProcessingException {

        Movie movie = getMovie(movieList, Id);
        movieProducer.sendMovieRecord(movie);
    }

    public CompletableFuture<SendResult<Integer, String>> sendMovie(Movie movie) throws JsonProcessingException {

        return movieProducer.sendMovieRecord(movie);
    }
}
