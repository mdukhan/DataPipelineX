package org.example.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.entities.Movie;
import org.example.repositories.MovieRepository;
import org.example.services.MovieService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * Integration test class for the MovieConsumer component.
 * <p>
 * This test class covers the integration of the MovieConsumer component by utilizing an embedded Kafka environment.
 * It verifies the behavior of publishing a new movie to the "movies" topic and ensures that the associated
 * MovieService and MovieRepository are correctly invoked.
 * </p>
 * <p>
 * The test scenario involves creating a JSON representation of a movie and publishing it to the Kafka topic.
 * The MovieConsumer listens to the topic, processes the incoming message, and invokes the MovieService to save the
 * movie details in the MovieRepository. The test then asserts that the movie is successfully persisted in the repository.
 * </p>
 */
@SpringBootTest
@EmbeddedKafka(topics = {"movies","error-topic","movie-response-topic"}, partitions = 1)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}", "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}"})
class MovieConsumerTest {

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    KafkaListenerEndpointRegistry endpointRegistry;

    @SpyBean
    MovieConsumer movieConsumerSpy;

    @SpyBean
    MovieService movieServiceSpy;

    @Autowired
    MovieRepository movieRepository;

    /**
     * Sets up the test environment by waiting for Kafka message listener containers to be assigned to partitions.
     * This ensures that the containers are ready to process messages before the test begins.
     */
    @BeforeEach
    void setUp() {
        for (MessageListenerContainer messageListenerContainer : endpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }

    /**
     * Cleans up the test environment by deleting all movies from the MovieRepository after each test execution.
     */
    @AfterEach
    void tearDown() {
        movieRepository.deleteAll();
    }

    /**
     * Tests the scenario of publishing a new movie to the "movies" topic and verifying its correct processing.
     * <p>
     * The test involves sending a JSON representation of a movie to the Kafka topic and then waiting for the
     * MovieConsumer to process the message. The assertions verify that the MovieConsumer's `onMessage` method and the
     * associated MovieService's `processMovie` method are correctly invoked. Finally, the test checks that the movie
     * details are successfully persisted in the MovieRepository.
     * </p>
     *
     * @throws ExecutionException      If an execution exception occurs.
     * @throws InterruptedException    If the thread is interrupted.
     * @throws JsonProcessingException If there's an issue processing JSON data.
     */
    @Test
    void publishNewMovie() throws ExecutionException, InterruptedException, JsonProcessingException {
        // Given a JSON representation of a movie
        String json= "{\"Id\":1,\"movieType\":\"NEW\",\"title\":\"Inception\",\"genres\":\"Sci-Fi\"}";
        // When publishing the movie to the "movies" topic
        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>("movies", 1, json);
        kafkaTemplate.send(producerRecord);

        // Wait for the MovieConsumer to process the message
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        // Then verify the correct method invocations and movie persistence
        verify(movieConsumerSpy, times(1)).onMessage(isA(ConsumerRecord.class));
        verify(movieServiceSpy, times(1)).processMovie(isA(ConsumerRecord.class));

        List<Movie> movieList = (List<Movie>) movieRepository.findAll();
        assert movieList.size() == 1;
        movieList.forEach(movie -> {
            assert movie.getId() != null;
            assertEquals(1, movie.getId());
        });
    }
}
