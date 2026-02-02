package com.hammperpulse.auction.kafka.messaging.dto;
import com.hammperpulse.auction.entity.Auction;

public record AuctionEndingDomainEvent(Auction auction) {

}