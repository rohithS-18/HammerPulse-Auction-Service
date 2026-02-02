package com.hammperpulse.auction.dto;

import lombok.Data;

@Data
public class WinnerDetails {
    private int bidderId;
    private double amount;
    private String finalStatus;
}
