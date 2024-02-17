package org.example.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.records.Movie;
import org.example.records.Rating;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Component class for producing messages to a Kafka topic related to movies.
 */
@Component
@Slf4j
public class RecordsProducer {

    ObjectMapper objectMapper;
    @Value("${spring.kafka.topics.movies}")
    private String moviesTopic;

    @Value("${spring.kafka.topics.ratings}")
    private String ratingsTopic;
    private KafkaTemplate<Integer, String> kafkaTemplate;

    public RecordsProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Asynchronously sends a movie record to the configured Kafka topic.
     *
     * @param movie The Movie object to be sent.
     * @return A CompletableFuture representing the result of the send operation.
     */
    public CompletableFuture<SendResult<Integer, String>> sendMovieRecord(Movie movie) throws JsonProcessingException {
        Integer key = movie.Id();
        String jsonValue = objectMapper.writeValueAsString(movie);

        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>(moviesTopic, key, jsonValue);
        var completableFuture = kafkaTemplate.send(producerRecord);
        return completableFuture
                .whenComplete((sendResult, throwable) -> {
                    if (throwable != null) {
                        handleFailure( throwable);
                    } else {
                        handleSuccess(key, jsonValue);
                    }
                });
    }

    public CompletableFuture<SendResult<Integer, String>> sendRatingRecord(Rating rating) throws JsonProcessingException {
        //try {
            String jsonValue = objectMapper.writeValueAsString(rating);

            ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>(ratingsTopic, jsonValue);
            var completableFuture = kafkaTemplate.send(producerRecord);
            return completableFuture
                    .whenComplete((sendResult, throwable) -> {
                        if (throwable != null) {
                            handleFailure(throwable);
                        } else {
                            handleSuccess(jsonValue);
                        }
                    });
       // }

        //catch (NullPointerException e){
        //}
       // return null;
    }



    /**
     * Handles failure during the asynchronous send operation.
     *
     * @param key   The key of the message.
     * @param value The value of the message.
     * @param ex    The exception that occurred.
     */
    private void handleFailure(Throwable ex) {
        log.error("Error sending the Message and the exception is {}", ex.getMessage());
    }

    /**
     * Handles success during the asynchronous send operation.
     *
     * @param key    The key of the message.
     * @param value  The value of the message.
     * @param result The result of the send operation.
     */
    private void handleSuccess(Integer key, String value) {
        log.info("Message sent successfully for the key : {} and the value is {} , partition is {}", key, value);
    }

    private void handleSuccess(String value) {
        handleSuccess(null, value);
    }
}
