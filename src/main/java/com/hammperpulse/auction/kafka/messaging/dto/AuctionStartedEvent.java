package com.hammperpulse.auction.kafka.messaging.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AuctionStartedEvent {
    private int auctionId;
    private int sellerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int startingPrice;
    private int minInc;
}
