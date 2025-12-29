package com.hammperpulse.auction.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Discovery {
    @Bean
    @LoadBalanced
    public org.springframework.web.client.RestTemplate getRestTemplate(){
        return new org.springframework.web.client.RestTemplate();
    }
}
