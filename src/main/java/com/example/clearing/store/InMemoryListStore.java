package com.example.clearing.store;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryListStore implements ListStore {

    private final ConcurrentMap<String, Deque<String>> map = new ConcurrentHashMap<>();

    @Override
    public String leftPop(String key) {
        Deque<String> dq = map.get(key);
        if (dq == null) return null;
        synchronized (dq) {
            return dq.pollFirst();
        }
    }

    @Override
    public void rightPush(String key, String value) {
        map.computeIfAbsent(key, k -> new ArrayDeque<>());
        Deque<String> dq = map.get(key);
        synchronized (dq) {
            dq.addLast(value);
        }
    }

    @Override
    public void leftPush(String key, String value) {
        map.computeIfAbsent(key, k -> new ArrayDeque<>());
        Deque<String> dq = map.get(key);
        synchronized (dq) {
            dq.addFirst(value);
        }
    }

    @Override
    public long size(String key) {
        Deque<String> dq = map.get(key);
        return dq == null ? 0 : dq.size();
    }
}