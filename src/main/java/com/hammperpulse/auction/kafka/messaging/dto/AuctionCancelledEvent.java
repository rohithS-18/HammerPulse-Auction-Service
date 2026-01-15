package com.hammperpulse.auction.kafka.messaging.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuctionCancelledEvent {
    private int auctionId;
    private String cancelledBy;
    private String reason;
}
