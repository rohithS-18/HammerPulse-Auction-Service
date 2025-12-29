package com.hammperpulse.auction.exception;

public class DataIntegrityViolation extends RuntimeException{
    public DataIntegrityViolation(String message){
        super(message);
    }
}
