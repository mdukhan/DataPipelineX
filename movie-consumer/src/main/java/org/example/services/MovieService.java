package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.entities.Movie;
import org.example.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MovieService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MovieRepository movieRepository;

    public void processMovie(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        Movie movie = objectMapper.readValue(consumerRecord.value(), Movie.class);
        log.info("Movie : {} ", movie);
        save(movie);
    }


    private void save(Movie movie) {
        movieRepository.save(movie);
        log.info("successfully persisted the movie {} ", movie);
    }
}
