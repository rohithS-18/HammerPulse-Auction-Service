package com.hammperpulse.auction.dto;

import com.hammperpulse.auction.annotations.ValidEndTime;
import com.hammperpulse.auction.annotations.ValidUser;
import com.hammperpulse.auction.enums.AUCTION_STATUS;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ValidEndTime
public class AuctionDto {
    private int id;
    @NotNull
    private String title;
    private String description;
    @NotNull @Positive
    private int startingPrice;
    @NotNull @Positive
    private int minInc;
    @Future
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @ValidUser
    @NotNull
    private Integer sellerId;
    @Enumerated(EnumType.STRING)
    private AUCTION_STATUS status;


}
