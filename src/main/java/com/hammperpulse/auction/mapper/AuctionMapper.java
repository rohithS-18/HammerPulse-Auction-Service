package com.hammperpulse.auction.mapper;

import com.hammperpulse.auction.dto.AuctionDto;
import com.hammperpulse.auction.entity.Auction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuctionMapper {

    @Mapping(source = "sellerId", target = "sellerId")
    @Mapping(source="status", target="status")
    Auction toEntity(AuctionDto auctionDto);

    @Mapping(source = "sellerId", target = "sellerId")
    @Mapping(source="status", target="status")
    AuctionDto toDto(Auction auction);
}
