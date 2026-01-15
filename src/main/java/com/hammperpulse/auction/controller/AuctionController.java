package com.hammperpulse.auction.controller;

import com.hammperpulse.auction.dto.AuctionDto;
import com.hammperpulse.auction.kafka.messaging.producer.AuctionEventProducer;
import com.hammperpulse.auction.service.AuctionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuctionController {
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private AuctionEventProducer producerService;

    @PostMapping("/auction")
    public ResponseEntity<AuctionDto> createAuction(@Valid @RequestBody AuctionDto auction){
        return ResponseEntity.ok(auctionService.createAuction(auction));
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<AuctionDto> getAuctionById(@PathVariable("auctionId") int id){
        return ResponseEntity.ok(auctionService.getAuctionById(id));
    }

    @PatchMapping("/auction/{auctionId}/cancel")
    public ResponseEntity<?> cancelAuction(@PathVariable("auctionId") int id,@RequestBody String reason){
        auctionService.cancelAuction(id,reason);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("kafka/publish")
    public String publish(@RequestParam String message) {
        producerService.sendMessage(message);
        return "Message sent to Kafka";
    }

    @GetMapping("/user/{userId}/auctions")
    public ResponseEntity<List<AuctionDto>> getAuctionByUser(@PathVariable("userId") int id){
        return ResponseEntity.ok(auctionService.getAuctionByUser(id));
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(){
        return  ResponseEntity.ok("test");
    }

}
