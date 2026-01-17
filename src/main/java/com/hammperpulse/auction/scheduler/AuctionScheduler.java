package com.hammperpulse.auction.scheduler;

import com.hammperpulse.auction.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AuctionScheduler {
    @Autowired
    private AuctionService auctionService;

    @Scheduled(fixedDelay = 500000)
    public void pollAuctionToStart(){
        auctionService.StartEligibleAuctions();
    }

    @Scheduled(fixedDelay = 50000)
    public void pollAuctionToEnd(){
        auctionService.endEligibleAuctions();
    }
}
