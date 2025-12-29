package com.hammperpulse.auction.dto;

import com.hammperpulse.auction.enums.USER_STATUS;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class UserDto {
    private Integer id;
    private String name;
    private String username;
    private String email;
    private com.hammperpulse.auction.enums.USER_STATUS user_status;
    private Date dob;
    private String phoneNumber;
    private String profilePic;
}

