package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);

    /*
      First TODO:
      1.CSV Datei lesen
      2.Datei ins Object umwandeln
      2.Object Bytes mit Producer senden
      3.Kafka Broker bearbeitet dieses Object
      4.Kafka Consumer bearbeitet den Object
      5.Object in DB speichern

      Second TODO:
      Kafka as a docker container
      DB as a docker container

     */

  }

}