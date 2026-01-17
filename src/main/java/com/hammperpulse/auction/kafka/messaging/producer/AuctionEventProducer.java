package com.hammperpulse.auction.kafka.messaging.producer;

import com.hammperpulse.auction.entity.Auction;
import com.hammperpulse.auction.kafka.messaging.dto.*;
import com.hammperpulse.auction.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class AuctionEventProducer {
    @Autowired
    private EventMapper eventMapper;


    private final KafkaTemplate<String, EventEnvelope<?>> kafkaTemplate;

    public AuctionEventProducer(KafkaTemplate<String, EventEnvelope<?>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        EventEnvelope event=createEnvelope("TEST",message);
            kafkaTemplate.send("auction-events", event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            System.out.println("❌ Failed to send message: " + ex.getMessage());
                        } else {
                            System.out.println("✅ Message sent to Kafka: " + message);
                        }
                    });
        }
    public void publishAuctionCreated(Auction auction){
        AuctionCreatedEvent auctionCreatedEvent=eventMapper.tocreateEvent(auction);
        EventEnvelope event=createEnvelope("AUCTION_CREATED",auctionCreatedEvent);
        kafkaTemplate.send("auction-events",event);
    }

    public void publishAuctionStarted(Auction auction){
        AuctionStartedEvent auctionStartedEvent=eventMapper.toStartEvent(auction);
        EventEnvelope event=createEnvelope("AUCTION_STARTED",auctionStartedEvent);
        kafkaTemplate.send("auction-events",event);
    }

    public void publishAuctionEnded(Auction auction, String result,String winnerName, int winningPrice){
        AuctionEndedEvent auctionEndedEvent=eventMapper.toEndEvent(auction);
        auctionEndedEvent.setResult(result);
        auctionEndedEvent.setWinnerName(winnerName);
        auctionEndedEvent.setWinningPrice(winningPrice);
        EventEnvelope event=createEnvelope("AUCTION_ENDED",auctionEndedEvent);
        kafkaTemplate.send("auction-events",event);
    }
    public void publishAuctionCancelled(Auction auction,String reason){
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(reason);
        AuctionCancelledEvent auctionCancelledEvent=eventMapper.toCancelEvent(auction);
        auctionCancelledEvent.setReason(node.get("reason").asText());
        EventEnvelope event=createEnvelope("AUCTION_STARTED",auctionCancelledEvent);
        kafkaTemplate.send("auction-events",event);
    }

    public <T> EventEnvelope<T> createEnvelope(String eventType, T data){
        return new EventEnvelope<>(
                UUID.randomUUID().toString(),
                eventType,
                Instant.now(),
                1,
                "auction-service",
                data
        );
    }

}
