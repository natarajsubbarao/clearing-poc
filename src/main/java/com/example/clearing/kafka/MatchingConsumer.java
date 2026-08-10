package com.example.clearing.kafka;

import com.example.clearing.dto.ExecutionMessage;
import com.example.clearing.dto.TradeMessage;
import com.example.clearing.service.MatchingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MatchingConsumer {

    private final MatchingService matchingService;
    private final ObjectMapper objectMapper;

    public MatchingConsumer(MatchingService matchingService, ObjectMapper objectMapper) {
        this.matchingService = matchingService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${topic.trades:trades-normalized}", groupId = "matching-group")
    public void consumeTrade(String message) {
        try {
            TradeMessage t = objectMapper.readValue(message, TradeMessage.class);
            matchingService.handleTrade(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "${topic.execs:executions-normalized}", groupId = "matching-group")
    public void consumeExec(String message) {
        try {
            ExecutionMessage e = objectMapper.readValue(message, ExecutionMessage.class);
            matchingService.handleExecution(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
