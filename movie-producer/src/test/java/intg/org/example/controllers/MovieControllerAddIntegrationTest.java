package org.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.records.Movie;
import org.example.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;


import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the MovieController class. This class focuses on testing the interactions
 * between the application and Kafka for movie-related operations.
 *
 * <p>The tests leverage the EmbeddedKafkaBroker for a lightweight and isolated Kafka environment.
 * Different ports are used for each broker to ensure that records from previous tests do not
 * interfere with the current test. This helps in avoiding unintended consumption of records
 * from unrelated topics, providing a clean and isolated testing environment for each scenario.
 *
 **/
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"movies"}, partitions =1, ports = 9091)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
class MovieControllerAddIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    ObjectMapper objectMapper;

    private Consumer<Integer, String> consumer;

    /**
     * Setup method executed before each test.
     * Initializes a Kafka consumer and configures it to consume from all embedded topics.
     */
    @BeforeEach
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    /**
     * Teardown method executed after each test.
     * Closes the Kafka consumer.
     */
    @AfterEach
    void tearDown() {
        consumer.close();
    }


    /**
     * Integration test for the POST endpoint to add a Movie.
     * Sends a POST request, verifies the HTTP response, and checks the Kafka topic for the added movie.
     *
     * @throws JsonProcessingException if there is an issue processing JSON
     */
    @Test
    public void addMovie() throws JsonProcessingException {

        Movie movie = TestUtil.movieNewRecord();

        HttpHeaders headers = new HttpHeaders();
        String movieJson = objectMapper.writeValueAsString(movie);
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<Movie> request = new HttpEntity<>(movie, headers);

        var responseEntity = restTemplate.exchange("/add/movie", HttpMethod.POST, request, Movie.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        //Instantiate a consumer
        // Read the record , assert the count and parse the record and assert on it.
        ConsumerRecords<Integer, String> consumerRecords = KafkaTestUtils.getRecords(consumer);
        //Thread.sleep(3000);
        assert consumerRecords.count() == 1;
        consumerRecords.forEach(record -> {
            assertEquals(movieJson, record.value());
        });
    }
}