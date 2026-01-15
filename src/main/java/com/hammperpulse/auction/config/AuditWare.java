package com.hammperpulse.auction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditWare {
    @Bean
    public AuditorAware<String> auditorAware(){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth==null)
            return () ->Optional.ofNullable("System");
        return () -> Optional.ofNullable(
                SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
