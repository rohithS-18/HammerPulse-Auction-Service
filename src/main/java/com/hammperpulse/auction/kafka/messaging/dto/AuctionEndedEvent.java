package com.hammperpulse.auction.kafka.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AuctionEndedEvent {
    private Long auctionId;
    private Long sellerId;
    private LocalDateTime endTime;
    private String result;
    private int winnerName;
    private double winningPrice;
}
