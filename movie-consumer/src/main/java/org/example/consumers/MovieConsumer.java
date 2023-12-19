package org.example.consumers;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MovieConsumer {

    @KafkaListener(topics= {"movies"})
public void onMessage(ConsumerRecord<Integer,String> consumerRecord) {

        log.info("ConsumerRecord : {}",consumerRecord);

}
}
