package com.example.clearing.store;

public interface ListStore {
    String leftPop(String key);
    void rightPush(String key, String value);
    void leftPush(String key, String value);
    long size(String key);
}