package com.hammperpulse.auction.kafka.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionEndedEvent {
    private Long auctionId;
    private Long sellerId;
    private LocalDateTime endTime;
    private String result;
    private int winnerName;
    private double winningPrice;
}
