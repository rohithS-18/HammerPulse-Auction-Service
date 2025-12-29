package com.hammperpulse.auction.repository;

import com.hammperpulse.auction.entity.Auction;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.List;

@RepositoryDefinition(domainClass = Auction.class, idClass = Integer.class)
public interface AuctionRepo {
    Auction save(Auction auction);

    Auction saveAndFlush(Auction entity);

    Auction findById(int id);

    List<Auction> findBySellerId(int id);
}
