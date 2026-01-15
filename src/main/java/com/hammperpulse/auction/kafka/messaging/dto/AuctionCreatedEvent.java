package com.hammperpulse.auction.kafka.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AuctionCreatedEvent {
    private Long auctionId;
    private Long sellerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal startingPrice;
    private BigDecimal minInc;
}
