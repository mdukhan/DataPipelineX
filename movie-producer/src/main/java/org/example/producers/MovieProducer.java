package org.example.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.records.Movie;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MovieProducer {


    @Value("${spring.kafka.topic}")
    private String topic;

    @Autowired
    public MovieProducer(KafkaTemplate<Integer, String> kafkaTemplate,ObjectMapper objectMapper){
        this.kafkaTemplate=kafkaTemplate;
        this.objectMapper=objectMapper;
    }

    private KafkaTemplate<Integer, String> kafkaTemplate;

    ObjectMapper objectMapper;


    /**
     * Asynchronously sends a movie record to the configured Kafka topic.
     *
     * @param movie The Movie object to be sent.
     * @return A CompletableFuture representing the result of the send operation.
     */
    public CompletableFuture<SendResult<Integer, String>> sendMovieRecord(Movie movie) throws JsonProcessingException {
        Integer key = movie.Id();
        String jsonValue = objectMapper.writeValueAsString(movie);

        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>(topic, key, jsonValue);
        var completableFuture = kafkaTemplate.send(producerRecord);
        return completableFuture
                .whenComplete((sendResult, throwable) -> {
                    if (throwable != null) {
                        handleFailure(key, jsonValue, throwable);
                    } else {
                        handleSuccess(key, jsonValue, sendResult);
                    }
                });
    }

    /**
     * Handles failure during the asynchronous send operation.
     *
     * @param key   The key of the message.
     * @param value The value of the message.
     * @param ex    The exception that occurred.
     */
    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error sending the Message and the exception is {}", ex.getMessage());
    }

    /**
     * Handles success during the asynchronous send operation.
     *
     * @param key    The key of the message.
     * @param value  The value of the message.
     * @param result The result of the send operation.
     */
    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Message sent successfully for the key : {} and the value is {} , partition is {}", key, value);
    }

}
