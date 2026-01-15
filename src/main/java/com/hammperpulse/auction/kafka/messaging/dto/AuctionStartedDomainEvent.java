package com.hammperpulse.auction.kafka.messaging.dto;

import com.hammperpulse.auction.entity.Auction;

public record AuctionStartedDomainEvent(Auction auction) {
}
