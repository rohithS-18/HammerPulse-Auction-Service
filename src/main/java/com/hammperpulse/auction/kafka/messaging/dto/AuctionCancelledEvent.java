package com.hammperpulse.auction.kafka.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionCancelledEvent {
    private int auctionId;
    private String cancelledBy;
    private String reason;
}
