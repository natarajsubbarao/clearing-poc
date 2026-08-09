package com.example.clearing.producer;

import com.example.clearing.dto.ExecutionMessage;
import com.example.clearing.dto.TradeMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReaderHeaderAware;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileReader;
import java.util.Map;

@Component
public class CsvPublisher implements CommandLineRunner {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.trades.path}")
    private String tradesPath;

    @Value("${app.execs.path}")
    private String execsPath;

    @Value("${topic.trades}")
    private String tradesTopic;

    @Value("${topic.execs}")
    private String execsTopic;

    public CsvPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        publishTrades();
        publishExecs();
    }

    private void publishTrades() {
        try (CSVReaderHeaderAware r = new CSVReaderHeaderAware(new FileReader(tradesPath))) {
            Map<String, String> row;
            while ((row = r.readMap()) != null) {
                String tradeId = row.getOrDefault("trade_id", "").trim();
                String symbol = row.getOrDefault("symbol", "").trim();
                String side = row.getOrDefault("side", "BUY").trim();
                long qty = Long.parseLong(row.getOrDefault("qty", "0").trim());
                double price = Double.parseDouble(row.getOrDefault("price", "0").trim());
                TradeMessage t = new TradeMessage(tradeId, symbol, side, qty, price);
                String json = objectMapper.writeValueAsString(t);
                kafkaTemplate.send(tradesTopic, tradeId, json);
                Thread.sleep(100); // small throttle to simulate streaming
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publishExecs() {
        try (CSVReaderHeaderAware r = new CSVReaderHeaderAware(new FileReader(execsPath))) {
            Map<String, String> row;
            while ((row = r.readMap()) != null) {
                String execId = row.getOrDefault("exec_id", "").trim();
                String symbol = row.getOrDefault("symbol", "").trim();
                String side = row.getOrDefault("side", "BUY").trim();
                long qty = Long.parseLong(row.getOrDefault("qty", "0").trim());
                double price = Double.parseDouble(row.getOrDefault("price", "0").trim());
                ExecutionMessage e = new ExecutionMessage(execId, symbol, side, qty, price);
                String json = objectMapper.writeValueAsString(e);
                kafkaTemplate.send(execsTopic, execId, json);
                Thread.sleep(150); // small throttle
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}