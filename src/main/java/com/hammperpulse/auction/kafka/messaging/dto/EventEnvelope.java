package com.hammperpulse.auction.kafka.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class EventEnvelope<T> {
    public String eventId;
    public String eventType;
    public Instant occurredAt;
    public int version;
    public String producer;
    public T data;
}
