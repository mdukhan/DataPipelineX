package org.example.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.services.MovieService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class RatingConsumer {
    MovieService movieService;
    private final KafkaTemplate<Integer, String> kafkaTemplate;

    public RatingConsumer(MovieService movieService,KafkaTemplate<Integer,String> kafkaTemplate) {
        this.movieService = movieService;
        this.kafkaTemplate=kafkaTemplate;
    }

    @KafkaListener(topics = {"${spring.kafka.topics.ratings}"})
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        try {
            log.info("ConsumerRecord : {}", consumerRecord);
           // movieService.processMovie(consumerRecord);
        } catch (Exception e) {
            // Log the exception and send it to the error topic
            log.error(e.getMessage());
            kafkaTemplate.send("error-topic", e.getMessage());
        }
    }
}
