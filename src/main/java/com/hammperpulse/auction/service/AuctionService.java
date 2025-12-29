package com.hammperpulse.auction.service;

import com.hammperpulse.auction.dto.AuctionDto;
import com.hammperpulse.auction.entity.Auction;
import com.hammperpulse.auction.enums.AUCTION_STATUS;
import com.hammperpulse.auction.exception.DataIntegrityViolation;
import com.hammperpulse.auction.exception.IllegalStateException;
import com.hammperpulse.auction.exception.ResourceNotFoundException;
import com.hammperpulse.auction.mapper.AuctionMapper;
import com.hammperpulse.auction.repository.AuctionRepo;
import com.netflix.discovery.converters.Auto;
import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuctionService {
    @Autowired
    private AuctionRepo auctionRepo;
    @Autowired
    private AuctionMapper auctionMapper;

    public AuctionDto createAuction(AuctionDto auction) {
        try{
            if(auction.getStartTime()!=null && auction.getEndTime()!=null){
                auction.setStatus(AUCTION_STATUS.SCHEDULED);
            }
            Auction auction1=auctionMapper.toEntity(auction);
            System.out.println("SellerId in entity = " + auction1.getSellerId());
             auction1=auctionRepo.saveAndFlush(auction1);
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

    public void cancelAuction(int id) {
        Auction auction =auctionRepo.findById(id);
        AUCTION_STATUS curStatus=auction.getStatus();
        if(curStatus==AUCTION_STATUS.CREATED || curStatus==AUCTION_STATUS.SCHEDULED) {
            auction.setStatus(AUCTION_STATUS.CANCELLED);
            auctionRepo.save(auction);
        }
        else{
            throw new IllegalStateException("Auction not in created or scheduled state");
        }
    }
}
