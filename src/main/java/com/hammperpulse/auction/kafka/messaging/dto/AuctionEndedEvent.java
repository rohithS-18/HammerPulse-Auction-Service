package com.hammperpulse.auction.kafka.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AuctionEndedEvent {
    private Long auctionId;
    private Long sellerId;
    private LocalDateTime endTime;
    private String result;
    private String winnerName;
    private int winningPrice;
}
