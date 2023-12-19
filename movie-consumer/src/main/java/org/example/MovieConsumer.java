package org.example;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Service
public class MovieConsumer {
    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.group-id}")
    private String groupId;

    @Value("${kafka.consumer.topic}")
    private String topic;

    // Database interaction logic (e.g., saving to a database) can be injected here

    public void consumeMessages() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        try (Consumer<String, String> consumer = new KafkaConsumer<>(properties)) {
            consumer.subscribe(Collections.singletonList(topic));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                records.forEach(record -> {
                    // Deserialize the message and save to the database
                    String movieData = record.value();
                    MovieEntity movieEntity = deserializeAndSaveToDatabase(movieData);

                    // Perform database operations with the movieEntity
                    // (e.g., save it to a database using Spring Data JPA)
                    // ...

                    System.out.println("Received and saved message: " + movieEntity);
                });
            }
        }
    }

    private MovieEntity deserializeAndSaveToDatabase(String movieData) {
        // Deserialize the JSON or CSV data and create a MovieEntity
        // ...

        // For simplicity, let's assume a method to create a MovieEntity
        return createMovieEntity(movieData);
    }

    private MovieEntity createMovieEntity(String movieData) {
        // Implementation to create a MovieEntity from the deserialized data
        // ...

        return new MovieEntity();
    }
}
