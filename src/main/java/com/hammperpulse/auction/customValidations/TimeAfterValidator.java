package com.hammperpulse.auction.customValidations;

import com.hammperpulse.auction.annotations.ValidEndTime;
import com.hammperpulse.auction.dto.AuctionDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class TimeAfterValidator implements ConstraintValidator<ValidEndTime, AuctionDto> {


    @Override
    public void initialize(ValidEndTime constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(AuctionDto auctionDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime endTime=auctionDto.getEndTime();
        LocalDateTime startTime=auctionDto.getStartTime();
        if(endTime==null)   return true;
        return startTime!=null && endTime.isAfter(startTime);
    }
}
