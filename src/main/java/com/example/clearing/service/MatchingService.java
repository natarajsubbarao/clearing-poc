package com.example.clearing.service;

import com.example.clearing.dto.ExecutionMessage;
import com.example.clearing.dto.TradeMessage;
import com.example.clearing.store.ListStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MatchingService {

    private final ListStore store;
    private final ObjectMapper objectMapper;

    public MatchingService(ListStore store, ObjectMapper objectMapper) {
        this.store = store;
        this.objectMapper = objectMapper;
    }

    // composite key: symbol|side|price
    private String keyFor(String symbol, String side, double price) {
        return String.format("match:%s:%s:%s", symbol, side, Double.toString(price));
    }

    public void handleTrade(TradeMessage trade) throws Exception {
        String key = keyFor(trade.symbol, trade.side, trade.price);
        // try match against executions
        matchAgainstExecutions(trade, key);
        // if remaining, push to trades list tail
        if (trade.remaining > 0) {
            String json = objectMapper.writeValueAsString(trade);
            store.rightPush(key + ":trades", json);
        }
    }

    private void matchAgainstExecutions(TradeMessage trade, String key) throws Exception {
        while (trade.remaining > 0) {
            String execJson = store.leftPop(key + ":execs");
            if (!StringUtils.hasText(execJson)) break;
            ExecutionMessage exec = objectMapper.readValue(execJson, ExecutionMessage.class);

            long matched = Math.min(trade.remaining, exec.remaining);
            trade.remaining -= matched;
            exec.remaining -= matched;

            printMatch(trade.tradeId, exec.execId, trade.symbol, trade.side, matched, trade.price);

            if (exec.remaining > 0) {
                // push leftover execution back to head (it was popped from head; push back so it will be seen next)
                String remainingJson = objectMapper.writeValueAsString(exec);
                store.leftPush(key + ":execs", remainingJson);
            }
        }
    }

    public void handleExecution(ExecutionMessage exec) throws Exception {
        String key = keyFor(exec.symbol, exec.side, exec.price);
        // try match against trades
        matchAgainstTrades(exec, key);
        // if remaining, push to executions list tail
        if (exec.remaining > 0) {
            String json = objectMapper.writeValueAsString(exec);
            store.rightPush(key + ":execs", json);
        }
    }

    private void matchAgainstTrades(ExecutionMessage exec, String key) throws Exception {
        while (exec.remaining > 0) {
            String tradeJson = store.leftPop(key + ":trades");
            if (!StringUtils.hasText(tradeJson)) break;
            TradeMessage trade = objectMapper.readValue(tradeJson, TradeMessage.class);

            long matched = Math.min(exec.remaining, trade.remaining);
            exec.remaining -= matched;
            trade.remaining -= matched;

            printMatch(trade.tradeId, exec.execId, exec.symbol, exec.side, matched, exec.price);

            if (trade.remaining > 0) {
                // push back the leftover trade
                String remainingJson = objectMapper.writeValueAsString(trade);
                store.leftPush(key + ":trades", remainingJson);
            }
        }
    }

    private void printMatch(String tradeId, String execId, String symbol, String side, long qty, double price) {
        String out = String.format("MATCH -> trade=%s exec=%s symbol=%s side=%s qty=%d price=%s",
                tradeId, execId, symbol, side, qty, Double.toString(price));
        System.out.println(out);
    }
}