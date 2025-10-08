package com.jpmc.midascore.kafka;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-tasks")
    public void listen(Transaction transaction) {
        System.out.println("Received transaction for Task 2: " + transaction);
    }
}
