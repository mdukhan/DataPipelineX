package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class Producer {

  private static final Logger LOG = LoggerFactory.getLogger(App.class);

  @Value("${kafka.producer.topic}")
  private String topic;

  @Autowired
  private KafkaTemplate<String, String> template;

  public void send(String message) {
    template.send(topic, message);
    LOG.info("Sent [{}] to topic [{}]", message, topic);
  }

}
