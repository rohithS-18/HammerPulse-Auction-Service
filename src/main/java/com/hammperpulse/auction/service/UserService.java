package com.hammperpulse.auction.service;

import com.hammperpulse.auction.config.Discovery;
import com.hammperpulse.auction.dto.UserDto;
import com.hammperpulse.auction.enums.USER_STATUS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Service
public class UserService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private JWTService jwtService;

    public Boolean isvalidUser(int userId){
        try {
            HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setBearerAuth(jwtService.generateToken());
            HttpEntity<Void> entity =new HttpEntity<>(headers);
            ResponseEntity<UserDto> user=restTemplate.exchange(
                    "http://USER-SERVICE/internal/user/" + userId,
                    HttpMethod.GET,
                    entity,
                    UserDto.class
            );
            return user.getBody()!=null && user.getBody().getUser_status() != USER_STATUS.BLOCKED;

        } catch (Exception ex) {
            System.out.println("Exception while calling user details "+userId+" "+ex);
            return false;
        }

    }
}
