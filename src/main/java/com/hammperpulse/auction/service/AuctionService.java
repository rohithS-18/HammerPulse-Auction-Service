package com.hammperpulse.auction.service;

import com.hammperpulse.auction.dto.AuctionDto;
import com.hammperpulse.auction.entity.Auction;
import com.hammperpulse.auction.enums.AUCTION_STATUS;
import com.hammperpulse.auction.exception.DataIntegrityViolation;
import com.hammperpulse.auction.exception.ResourceNotFoundException;
import com.hammperpulse.auction.kafka.messaging.dto.AuctionStartedDomainEvent;
import com.hammperpulse.auction.kafka.messaging.dto.AuctionStartedEvent;
import com.hammperpulse.auction.kafka.messaging.producer.AuctionEventProducer;
import com.hammperpulse.auction.mapper.AuctionMapper;
import com.hammperpulse.auction.repository.AuctionRepo;
import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionService {
    @Autowired
    private AuctionRepo auctionRepo;
    @Autowired
    private AuctionMapper auctionMapper;
    @Autowired
    private AuctionEventProducer auctionEventProducer;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public AuctionDto createAuction(AuctionDto auction) {
        try{
            if(auction.getStartTime()!=null && auction.getEndTime()!=null){
                auction.setStatus(AUCTION_STATUS.SCHEDULED);
            }
            Auction auction1=auctionMapper.toEntity(auction);
            auction1=auctionRepo.saveAndFlush(auction1);
            auctionEventProducer.publishAuctionCreated(auction1);
            return auctionMapper.toDto(auction1);
        }catch(DataIntegrityViolationException ex){
            Throwable cause=ex.getCause();
            if(cause instanceof PropertyValueException pve){
                String field=pve.getPropertyName();
                throw new DataIntegrityViolation(field+" cannot be null");
            }
            throw new DataIntegrityViolation("error creating auction");
        }
    }


    public  AuctionDto getAuctionById(int id) {
        Auction auction =auctionRepo.findById(id);
        if(auction==null)
            throw new ResourceNotFoundException("No auction found with this id "+id);
        return auctionMapper.toDto(auction);
    }

    public List<AuctionDto> getAuctionByUser(int id) {
        List<Auction> auctions=auctionRepo.findBySellerId(id);
        if(auctions==null ||auctions.isEmpty())
            throw new ResourceNotFoundException("No auction created by this user "+id);
        return auctions.stream().map(auction->
                    auctionMapper.toDto(auction)
                ).toList();
    }

    public void cancelAuction(int id,String reason) {
        Auction auction =auctionRepo.findById(id);
        AUCTION_STATUS curStatus=auction.getStatus();
        if(curStatus==AUCTION_STATUS.CREATED || curStatus==AUCTION_STATUS.SCHEDULED) {
            auction.setStatus(AUCTION_STATUS.CANCELLED);
            auctionRepo.save(auction);
            auctionEventProducer.publishAuctionCancelled(auction,reason);
        }
        else{
            throw new IllegalStateException("Auction not in created or scheduled state");
        }
    }
    @Transactional
    public void StartEligibleAuctions() {
        System.out.println("here");
        List<Auction> eligibleAuctions =auctionRepo.findByStartTimeBeforeAndStatus(LocalDateTime.now(),AUCTION_STATUS.SCHEDULED);
        if(eligibleAuctions!=null){
            for(Auction auction : eligibleAuctions){
                auction.setStatus(AUCTION_STATUS.LIVE);
                auctionRepo.save(auction);
                applicationEventPublisher.publishEvent(new AuctionStartedDomainEvent(auction));
            }
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAuctionStarted(AuctionStartedDomainEvent auctionStartedDomainEvent){
        auctionEventProducer.publishAuctionStarted(auctionStartedDomainEvent.auction());
    }
}
