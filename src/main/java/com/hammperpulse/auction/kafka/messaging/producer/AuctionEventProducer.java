package com.hammperpulse.auction.kafka.messaging.producer;

import com.hammperpulse.auction.entity.Auction;
import com.hammperpulse.auction.enums.AUCTION_STATUS;
import com.hammperpulse.auction.kafka.messaging.dto.*;
import com.hammperpulse.auction.mapper.EventMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
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
        log.info("Received request to publish auction created event");
        AuctionCreatedEvent auctionCreatedEvent=eventMapper.tocreateEvent(auction);
        EventEnvelope event=createEnvelope("AUCTION_CREATED",auctionCreatedEvent);
        kafkaTemplate.send("auction-events",event);
        log.info("Successfully published auction created event");
    }

    public void publishAuctionStarted(Auction auction){
        log.info("Received request to publish auction started event");
        AuctionStartedEvent auctionStartedEvent=eventMapper.toStartEvent(auction);
        EventEnvelope event=createEnvelope("AUCTION_STARTED",auctionStartedEvent);
        kafkaTemplate.send("auction-events",event);
        log.info("Successfully published auction started event");
    }

    public void publishAuctionEnded(Auction auction, String result,int winnerId, double winningPrice){
        log.info("Received request to publish auction ended event");
        AuctionEndedEvent auctionEndedEvent=eventMapper.toEndEvent(auction);
        auctionEndedEvent.setResult(result);
        auctionEndedEvent.setWinnerName(winnerId);
        auctionEndedEvent.setWinningPrice(winningPrice);
        EventEnvelope event=createEnvelope("AUCTION_ENDED",auctionEndedEvent);
        kafkaTemplate.send("auction-events",event);
        log.info("Successfully published auction ended event");
    }
    public void publishAuctionCancelled(Auction auction,String reason){
        log.info("Received request to publish auction cancelled event");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(reason);
        AuctionCancelledEvent auctionCancelledEvent=eventMapper.toCancelEvent(auction);
        auctionCancelledEvent.setReason(node.get("reason").asText());
        EventEnvelope event=createEnvelope("AUCTION_CANCELLED",auctionCancelledEvent);
        kafkaTemplate.send("auction-events",event);
        log.info("Successfully published auction cancelled event");
    }

    public void publishDummy(String type){
        if(type.equals("cx")){
            log.info("Received request to publish auction cancelled event");
            AuctionCancelledEvent auctionCancelledEvent=new AuctionCancelledEvent(111,"System","test-notification");
            EventEnvelope cancelEvent=createEnvelope("TEST-Cancelled",auctionCancelledEvent);
            kafkaTemplate.send("auction-events",cancelEvent);
        }
        else if(type.equals("st")){
            AuctionStartedEvent event = new AuctionStartedEvent(
                    101,                          // auctionId
                    1111,                         // sellerId
                    LocalDateTime.now(),           // startTime
                    LocalDateTime.now().plusHours(2), // endTime
                    0,                         // startingPrice
                    0,                           // minInc
                    AUCTION_STATUS.LIVE        // status
            );

            EventEnvelope startEvent=createEnvelope("TEST_STARTED",event);
            kafkaTemplate.send("auction-events",startEvent);
        }
        else if(type.equals("end")){
            AuctionEndedEvent auctionEndedEvent=new AuctionEndedEvent();
            auctionEndedEvent.setResult("test");
            auctionEndedEvent.setWinnerName(0);
            auctionEndedEvent.setWinningPrice(0.0);
            EventEnvelope event=createEnvelope("TEST-ENDED",auctionEndedEvent);
            kafkaTemplate.send("auction-events",event);
        }
        log.info("Successfully published auction started event");
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
