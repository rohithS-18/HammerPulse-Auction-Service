package com.hammperpulse.auction.entity;

import com.hammperpulse.auction.enums.AUCTION_STATUS;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private String title;
    private String description;
    @Column(name = "starting_price")
    @NotNull
    private int startingPrice;
    @Column(name = "min_Inc")
    @NotNull
    private int minInc;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @CreatedBy
    @Column(name = "created_user")
    private String createdBy;
    @LastModifiedBy
    @Column(name = "updated_user")
    private String updatedBy;
    @Column(name = "seller_id")
    @NotNull
    private Integer sellerId;
    @Enumerated(EnumType.STRING)
    private AUCTION_STATUS status;
    @Version
    private int version;
    @PrePersist
    public void setDefaults(){
        if(status==null){
            status=AUCTION_STATUS.CREATED;
        }
    }
}
