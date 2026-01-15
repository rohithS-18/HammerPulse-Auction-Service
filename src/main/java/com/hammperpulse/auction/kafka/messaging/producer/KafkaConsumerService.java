package com.hammperpulse.auction.kafka.messaging.producer;

import jakarta.annotation.PostConstruct;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    @PostConstruct
    public void init() {
        System.out.println("✅ KafkaConsumerService initialized");
    }
    @KafkaListener(topics = "test-topic", groupId = "demo-group")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
    }
}

