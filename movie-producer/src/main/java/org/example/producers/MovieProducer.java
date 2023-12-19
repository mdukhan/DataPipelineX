package org.example.producers;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.records.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MovieProducer {


  @Value("${spring.kafka.topic}")
  private String topic;

  @Autowired
  private KafkaTemplate<Integer, String> template;

  public void sendmovieRecordwithId(Movie movie) {
    ProducerRecord<Integer, String> record = new ProducerRecord<>(topic, movie.Id(), movie.toString());
    template.send(record);
    log.info("Sent [{}] to topic [{}]", movie, topic);
  }

  public void send(String message) {
    template.send(topic,message);
    log.info("Sent [{}] to topic [{}]", message, topic);
  }

}
