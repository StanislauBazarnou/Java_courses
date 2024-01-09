package com.epam.course.trash;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LFUCache2 {
    private final Map<Integer, Integer> valueMap = new ConcurrentHashMap<>();
    private final Map<Integer, Long> lastAccessTimeMap = new ConcurrentHashMap<>();
    private final int maxSize;
    private final long evictionInterval = 5000; // 5 seconds in milliseconds
    private final long lastEvictionTime = 0; // initialize to 0

    public LFUCache2(int maxSize) {
        this.maxSize = maxSize;
    }

    public Integer get(int key) {
        if (valueMap.containsKey(key)) {
            // If the key is in the cache, update the last access time
            lastAccessTimeMap.put(key, System.currentTimeMillis());
            return valueMap.get(key);
        }
        // If the key is not in the cache, check if the cache has reached its maximum size
        if (valueMap.size() >= maxSize) {
            // If the cache is full, evict the least frequently used key that was accessed more than 5 seconds ago
            int leastFrequentlyUsedKey = getLeastFrequentlyUsedKey();
            if (System.currentTimeMillis() - lastAccessTimeMap.get(leastFrequentlyUsedKey) > evictionInterval) {
                valueMap.remove(leastFrequentlyUsedKey);
                lastAccessTimeMap.remove(leastFrequentlyUsedKey);
            }
        }
        // If the key is not in the cache and the cache is not full, return -1
        return -1;
    }

    public void put(int key, int value) {
        if (valueMap.containsKey(key)) {
            // If the key is already in the cache, update the value and the last access time
            valueMap.put(key, value);
            lastAccessTimeMap.put(key, System.currentTimeMillis());
        } else {
            // If the key is not in the cache, check if the cache has reached its maximum size
            if (valueMap.size() >= maxSize) {
                // If the cache is full, evict the least frequently used key that was accessed more than 5 seconds ago
                int leastFrequentlyUsedKey = getLeastFrequentlyUsedKey();
                if (System.currentTimeMillis() - lastAccessTimeMap.get(leastFrequentlyUsedKey) > evictionInterval) {
                    valueMap.remove(leastFrequentlyUsedKey);
                    lastAccessTimeMap.remove(leastFrequentlyUsedKey);
                }
            }
            // If the key is not in the cache or the cache is not full, add the key and value to the cache
            valueMap.put(key, value);
            lastAccessTimeMap.put(key, System.currentTimeMillis());
        }
    }

    private int getLeastFrequentlyUsedKey() {
        // is used to calculate the least frequently used key in the cache
        int minUsageCount = Integer.MAX_VALUE;
        int minUsageKey = -1;
        for (Map.Entry<Integer, Long> entry : lastAccessTimeMap.entrySet()) {
            if (entry.getValue() < minUsageCount) {
                minUsageCount = Math.toIntExact(entry.getValue());
                minUsageKey = entry.getKey();
            }
        }
        return minUsageKey;
    }
}
