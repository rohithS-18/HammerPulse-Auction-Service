package com.hammperpulse.auction.repository;

import com.hammperpulse.auction.entity.Auction;
import com.hammperpulse.auction.enums.AUCTION_STATUS;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@RepositoryDefinition(domainClass = Auction.class, idClass = Integer.class)
public interface AuctionRepo {
    Auction save(Auction auction);

    Auction saveAndFlush(Auction entity);

    Auction findById(int id);

    List<Auction> findBySellerId(int id);

    List<Auction> findByStartTimeBeforeAndStatus(LocalDateTime time,AUCTION_STATUS status);
}
