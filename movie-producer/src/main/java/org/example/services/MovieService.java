package org.example.services;

import lombok.extern.slf4j.Slf4j;
import org.example.producers.MovieProducer;
import org.example.records.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MovieService {


    private final MovieProducer movieProducer;


    @Autowired
    public MovieService(MovieProducer movieProducer) throws FileNotFoundException {
        this.movieProducer = movieProducer;
    }


    public  Movie getMovie(List<Movie> movieRecordsList, int movieId) {
        for (Movie movieRecord : movieRecordsList) {
            if (movieId == movieRecord.Id()) {
                log.info(String.format("Es wurde der Film: %s ausgewählt.", movieRecord.title()));
                log.info(String.format(movieRecord.genres().contains("|") ? "Genres: %s" : "Genre: %s", movieRecord.genres()));
                log.info(movieRecord.toString());
                return movieRecord;
            }
        }
        return null;
    }

    public  List<Movie> getMovieListFromPath(final String filePath) throws FileNotFoundException {
        List<Movie> movieList = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(",");
                movieList.add(new Movie(Integer.parseInt(values[0]), values[1], values[2]));
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
                movieList.add(new Movie(Integer.parseInt(values[0]), values[1], values[2]));
            }
        }
        return movieList;
    }


    public void sendMoviebyId(List<Movie> movieList,int Id) throws FileNotFoundException {

        Movie movie = getMovie(movieList,Id);
        log.info(movie.toString());
        movieProducer.sendmovieRecordwithId(movie);
    }

}
