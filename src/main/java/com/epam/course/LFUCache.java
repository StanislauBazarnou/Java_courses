package com.epam.course;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
public class LFUCache<K, V> {
    @Getter
    private final Map<K, CacheItem<V>> cache = Collections.synchronizedMap(new HashMap<>());
    @Getter
    private final Map<K, Integer> countOfGetsMap = Collections.synchronizedMap(new HashMap<>());
    @Getter
    private final SortedMap<Integer, List<K>> frequencyMap = Collections.synchronizedSortedMap(new TreeMap<>());
    private final int capacity;
    private final long timeToLiveInMillis;
    @Getter
    private final LongAdder timeSpent = new LongAdder();
    Statistics statistics = new Statistics();

    public LFUCache(int capacity, int timeToLiveInSeconds) {
        this.capacity = capacity;
        this.timeToLiveInMillis = TimeUnit.MILLISECONDS.convert(timeToLiveInSeconds, TimeUnit.SECONDS);
    }

    public synchronized V get(K key) {
        if (!cache.containsKey(key)) {
            return null;
        }
        CacheItem<V> item = cache.get(key);
        long timeSinceLastAccess = System.currentTimeMillis() - item.timestamp;
        if (timeSinceLastAccess > timeToLiveInMillis) {
            log.info("Time since last access ({} ms) is greater than TTL: {} ms", timeSinceLastAccess, timeToLiveInMillis);
            return null;
        }
        calculateFrequency(key);
        return item.item;
    }

    public synchronized void put(K key, V value) {
        if (cache.containsKey(key)) {
            updateCacheValue(key, value);
            return;
        }
        if (cache.size() == capacity) {
            evictCache();
        }
        addValueToCache(key, value);
    }

    private void updateCacheValue(K key, V value) {
        cache.put(key, new CacheItem<>(value, System.currentTimeMillis()));
    }

    private void calculateFrequency(K key) {
        // Our key has been requested 10 times
        int countOfGetsForKey = countOfGetsMap.get(key);
        // List of keys, that has been requested for 10 times
        List<K> keysWithSameCountOfGets = frequencyMap.get(countOfGetsForKey);
        keysWithSameCountOfGets.remove(key);
        if (keysWithSameCountOfGets.isEmpty()) {
            frequencyMap.remove(countOfGetsForKey);
        }
        frequencyMap.computeIfAbsent(countOfGetsForKey + 1, k -> new LinkedList<>()).add(key);
        countOfGetsMap.put(key, countOfGetsForKey + 1);
    }

    private void addValueToCache(K key, V value) {
        long startTime = System.nanoTime();
        cache.put(key, new CacheItem<>(value, System.currentTimeMillis()));
        long timeTaken = System.nanoTime() - startTime;
        timeSpent.add(timeTaken);
        log.info("Time spent on adding new value to cache: " + timeSpent);
        countOfGetsMap.put(key, 0);
        frequencyMap.computeIfAbsent(0, k -> new LinkedList<>()).add(key);
    }

    public void getAverageTimeSpent() {
        long averageTimeSpent = timeSpent.sumThenReset() / (cache.size() + 1);
        statistics.setAverageInsertTime(averageTimeSpent);
        log.info("Average time spent: " + averageTimeSpent);
    }

    public void showStatistics() {
        log.info("Number of cache evictions: " + statistics.getNumberOfCacheEvictions());
    }

    private void evictCache() {
        int lowestCount = frequencyMap.firstKey();
        K keyToDelete = frequencyMap.get(lowestCount).get(0);

        CacheItem<V> item = cache.get(keyToDelete);
        long timeSinceLastAccess = System.currentTimeMillis() - item.timestamp;
        if (timeSinceLastAccess < timeToLiveInMillis) {
            log.info("Our Cache is full");
        }

        frequencyMap.get(lowestCount).remove(0);
        log.info("The " + lowestCount + " value is removed from cache");
        int currentEvictions = statistics.getNumberOfCacheEvictions();
        statistics.setNumberOfCacheEvictions(currentEvictions + 1);

        if (frequencyMap.get(lowestCount).isEmpty()) {
            frequencyMap.remove(lowestCount);
        }
        cache.remove(keyToDelete);
        countOfGetsMap.remove(keyToDelete);
        log.info("Following key has been evicted from the cache: " + keyToDelete);
    }
}
