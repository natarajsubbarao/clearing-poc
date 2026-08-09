package com.example.clearing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionMessage {
    public String execId;
    public String symbol;
    public String side;
    public long qty;
    public double price;
    public long remaining;

    public ExecutionMessage() {}

    public ExecutionMessage(String execId, String symbol, String side, long qty, double price) {
        this.execId = execId;
        this.symbol = symbol;
        this.side = side;
        this.qty = qty;
        this.price = price;
        this.remaining = qty;
    }
}