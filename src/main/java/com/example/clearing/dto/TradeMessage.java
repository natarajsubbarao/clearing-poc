package com.example.clearing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeMessage {
    public String tradeId;
    public String symbol;
    public String side; // BUY / SELL
    public long qty;
    public double price;
    public long remaining; // for partial fills

    public TradeMessage() {}

    public TradeMessage(String tradeId, String symbol, String side, long qty, double price) {
        this.tradeId = tradeId;
        this.symbol = symbol;
        this.side = side;
        this.qty = qty;
        this.price = price;
        this.remaining = qty;
    }
}