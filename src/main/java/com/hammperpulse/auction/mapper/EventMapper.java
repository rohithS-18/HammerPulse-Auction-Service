package com.hammperpulse.auction.mapper;

import com.hammperpulse.auction.entity.Auction;
import com.hammperpulse.auction.kafka.messaging.dto.AuctionCancelledEvent;
import com.hammperpulse.auction.kafka.messaging.dto.AuctionCreatedEvent;
import com.hammperpulse.auction.kafka.messaging.dto.AuctionEndedEvent;
import com.hammperpulse.auction.kafka.messaging.dto.AuctionStartedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.context.SecurityContextHolder;

@Mapper(componentModel = "spring")
public abstract class EventMapper {
    @Mapping(source = "id", target="auctionId")
    public abstract AuctionCreatedEvent tocreateEvent(Auction auction);

    @Mapping(source = "id", target="auctionId")
    public abstract AuctionStartedEvent toStartEvent(Auction auction);

    @Mapping(target = "cancelledBy" , expression="java(getUserId())")
    @Mapping(source = "id", target="auctionId")
    public abstract AuctionCancelledEvent toCancelEvent(Auction auction);

    @Mapping(source = "id", target="auctionId")
    public abstract AuctionEndedEvent toEndEvent(Auction auction);

    public String getUserId(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
