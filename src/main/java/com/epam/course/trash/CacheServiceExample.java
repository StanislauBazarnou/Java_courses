package com.epam.course.trash;//package org.example;
//
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class CacheService {
//    private int ttlInSeconds;
//    private EvictionPolicy evictionPolicy;
//
//    public CacheService(int ttlInSeconds, EvictionPolicy evictionPolicy) {
//        this.ttlInSeconds = ttlInSeconds;
//        this.evictionPolicy = evictionPolicy;
//
//        Thread expiryJob = new Thread(new ExpiryJob());
//        expiryJob.start();
//    }
//
//    List<Key> removedEntries;
//
//    Statistics statistics = new Statistics();
//
//    class Statistics {
//        // Average time spent for putting new values into cache
//        int avgTimeForPut = 0;
//        // Number of cache evictions
//        int evictions = 0;
//    }
//
//    public static class Key {
//        public String id;
//    }
//
//    public static class Entry {
//        public String name;
//        public Long putTimestamp;
//        public Long getTimestamp;
//    }
//
//    private static final int MAX_SIZE = 2;
//
//    Map<Key, Entry> cache = new HashMap<>();
//
//    public Entry get(Key key) {
//        Entry entry = cache.get(key);
//        if (entry != null) {
//            Long timestamp = Instant.now().getEpochSecond();
//            entry.getTimestamp = timestamp;
//
//            cache.put(key, entry);
//        }
//        return cache.get(key);
//    }
//
//    public void put(Key key, Entry entry) {
//        if (cache.size() >= MAX_SIZE) {
//            switch (evictionPolicy) {
//                case LEAST_RECENTLY_USED -> {
//                    Key keyToDelete = null;
//
//                    Long oldestTimestamp = 0L;
//                    for (Map.Entry<Key, Entry> candidate : cache.entrySet()) {
//                        if (oldestTimestamp == 0L) {
//                            oldestTimestamp = candidate.getValue().getTimestamp;
//                            keyToDelete = candidate.getKey();
//                        } else {
//                            if (oldestTimestamp > candidate.getValue().getTimestamp) {
//                                oldestTimestamp = candidate.getValue().getTimestamp;
//                                keyToDelete = candidate.getKey();
//                            }
//                        }
//                    }
//
//                    cache.remove(keyToDelete);
//                }
//                case LEAST_FREQUENTLY_USED -> {
//                    // TODO Implement later
//                }
//            }
//        }
//
//        Long timestamp = Instant.now().getEpochSecond();
//        entry.putTimestamp = timestamp;
//        entry.getTimestamp = timestamp;
//
//        cache.put(key, entry);
//    }
//
//    public void remove(Key key) {
//        cache.remove(key);
//    }
//
//    public Statistics getStats() {
//        return statistics;
//    }
//
//    class ExpiryJob implements Runnable {
//        @Override
//        public void run() {
//            while (true) {
//                try {
//                    Thread.sleep(1000L);
//                    expireEntries();
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//
//        void expireEntries() {
//            Long currentTimestamp = Instant.now().getEpochSecond();
//            Long expireTimestamp = currentTimestamp - ttlInSeconds;
//            System.out.println("Removing keys for timestamp: " + expireTimestamp);
//
//            List<Key> toRemove = new ArrayList<>();
//
//            cache.forEach((key, entry) -> {
//                if (entry.putTimestamp < expireTimestamp) {
//                    toRemove.add(key);
//                }
//            });
//
//            toRemove.stream().forEach(key -> {
//                cache.remove(key);
//                System.out.println("Removed key: " + key.id);
//            });
//        }
//    }
//}
