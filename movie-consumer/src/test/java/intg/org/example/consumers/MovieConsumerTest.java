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

@SpringBootTest
@EmbeddedKafka(topics = {"movies"}
        , partitions = 1)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
        , "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}"})
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

    @BeforeEach
    void setUp() {
        for (MessageListenerContainer messageListenerContainer : endpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }

    @AfterEach
    void tearDown() {

        movieRepository.deleteAll();
    }

    @Test
    void publishNewMovie() throws ExecutionException, InterruptedException, JsonProcessingException {
        //given
        String json = "{\n" +
                "    \"Id\": 1,\n" +
                "    \"title\": \"Inception\",\n" +
                "    \"genres\": \"Sci-Fi\"\n" +
                "}";
        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>("movies", 1, json);
        kafkaTemplate.send(producerRecord);

        //when
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        //then
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