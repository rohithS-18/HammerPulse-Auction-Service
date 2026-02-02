package com.hammperpulse.auction.service;

import com.hammperpulse.auction.dto.AuctionDto;
import com.hammperpulse.auction.dto.WinnerDetails;
import com.hammperpulse.auction.entity.Auction;
import com.hammperpulse.auction.enums.AUCTION_STATUS;
import com.hammperpulse.auction.exception.DataIntegrityViolation;
import com.hammperpulse.auction.exception.ResourceNotFoundException;
import com.hammperpulse.auction.kafka.messaging.dto.*;
import com.hammperpulse.auction.kafka.messaging.producer.AuctionEventProducer;
import com.hammperpulse.auction.mapper.AuctionMapper;
import com.hammperpulse.auction.repository.AuctionRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@Slf4j
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
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${AUCTION_QUEUE_KEY}")
    private String auctionEndQueue;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private RestTemplate restTemplate;

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
        log.info("Polling to start eligible auction");
        List<Auction> eligibleAuctions =auctionRepo.findByStartTimeBeforeAndStatus(LocalDateTime.now(),AUCTION_STATUS.SCHEDULED);
        if(eligibleAuctions!=null){
            for(Auction auction : eligibleAuctions){
                log.info("Eligible auction found to start "+ auction.getId());
                auction.setStatus(AUCTION_STATUS.LIVE);
                auctionRepo.save(auction);
                applicationEventPublisher.publishEvent(new AuctionStartedDomainEvent(auction));
            }
        }
    }

    @Transactional
    public void endEligibleAuctions(){
        log.info("Polling to end eligible auction");
        Set<String> dueAuctionIds=redisTemplate.opsForZSet()
                .rangeByScore(auctionEndQueue,0,System.currentTimeMillis());
        if(dueAuctionIds==null || dueAuctionIds.isEmpty())
            return;
        for(String auctionId : dueAuctionIds){
            System.out.println("auctions who reached their end time");
            System.out.println("auction-id "+auctionId);
            Auction auction=auctionRepo.findById(Integer.parseInt(auctionId));
            auction.setStatus(AUCTION_STATUS.ENDED);
            auctionRepo.save(auction);
            applicationEventPublisher.publishEvent(new AuctionEndingDomainEvent(auction));
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAuctionStarted(AuctionStartedDomainEvent auctionStartedDomainEvent){
        Auction auction=auctionStartedDomainEvent.auction();
        System.out.println("Publishing kakfa auction_end event for auction "+auction.getId()+" after successfully updating auction status in db");
        auctionEventProducer.publishAuctionStarted(auction);
        long endTimeMillis=auction.getEndTime().toInstant(ZoneOffset.UTC).toEpochMilli();
        redisTemplate.opsForZSet().add(auctionEndQueue,String.valueOf(auction.getId()),endTimeMillis);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase=TransactionPhase.AFTER_COMMIT)
    public void onAuctionEnding(AuctionEndingDomainEvent auctionEndingDomainEvent){
        Auction auction =auctionEndingDomainEvent.auction();
        log.info("Getting final details for auction "+auction.getId());
        WinnerDetails winnerDetails=getFinalAuctionDetails(auction.getId());
        log.info("Auction winner id "+winnerDetails.getBidderId());
        log.info("Auction winner amount "+winnerDetails.getBidderId());
        log.info("Auction final status "+winnerDetails.getFinalStatus());
        auction.setWinnerId(winnerDetails.getBidderId());
        auction.setFinalStatus(winnerDetails.getFinalStatus());
        auction.setWinningPrice(winnerDetails.getAmount());
        auctionRepo.save(auction);
        applicationEventPublisher.publishEvent(new AuctionEndedDomainEvent(auction));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAuctionEnded(AuctionEndedDomainEvent auctionEndedDomainEvent){
        Auction auction=auctionEndedDomainEvent.auction();
        auctionEventProducer.publishAuctionEnded(auction, auction.getFinalStatus(), auction.getWinnerId(), auction.getWinningPrice());
        log.info("Remove ended auction from cache "+auction.getId());
        redisTemplate.opsForZSet().remove(auctionEndQueue,String.valueOf(auction.getId()));
    }

    public WinnerDetails getFinalAuctionDetails(int auctionId){
        log.info("Getting final auction details from bid service");
        HttpHeaders headers=new HttpHeaders();
        headers.setBearerAuth(jwtService.generateToken());
        HttpEntity<Void> entity=new HttpEntity<>(headers);
        ResponseEntity<WinnerDetails> finalAuctionDetails=restTemplate.exchange(
                "http://BIDDING-SERVICE/getFinalAuctionDetails/"+auctionId,
                HttpMethod.GET,
                entity,
                WinnerDetails.class
        );
        return finalAuctionDetails.getBody();
    }
}
