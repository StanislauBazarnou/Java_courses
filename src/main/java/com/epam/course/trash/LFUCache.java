package com.epam.course.trash;//package org.example;
//
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
//    /*
//    Implementation of an LFU (Least Frequently Used) cache.
//    This cache stores key-value pairs, counts the number of times each key is accessed, and removes the least
//    frequently accessed keys when the cache reaches its limits. It uses three data structures: valueMap to store
//    keys and values, countMap to store keys and their corresponding access frequencies, and frequencyMap to store
//    frequencies and lists of keys with the corresponding frequency
//
//    When an already existing key is updated, the value of the key is updated,
//    it's removed from the current frequency list, added to a higher frequency list and has it's count
//    in countMap incremented. If the frequency list at a certain frequency is empty after removal of a key,
//    that frequency is removed from frequencyMap. This is only possible if the cache's size was set
//    to more than 0 (size > 0)
//    */
//
//public class LFUCache {
//
//    private final Map<Integer, Integer> valueMap = Collections.synchronizedMap(new HashMap<>());
//    private final Map<Integer, Integer> countMap = Collections.synchronizedMap(new HashMap<>());
//    private final Map<Integer, List<Integer>> frequencyMap = Collections.synchronizedMap(new TreeMap<>());
//    private final int size;
//    private final long evictionThreshold = TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS);
//    private long lastAccessTime;
//    private final Lock lock = new ReentrantLock();
//
//    public LFUCache(int size) {
//        this.size = size;
//    }
//
//    public synchronized int get(int key) {
//        lock.lock();
//        try {
//            if (!valueMap.containsKey(key) || size == 0) {
//                return -1;
//            }
//
//            long currentTime = System.currentTimeMillis();
//            long timeSinceLastAccess = currentTime - lastAccessTime;
//
//            if (timeSinceLastAccess > evictionThreshold) {
//                System.out.println("Time since last access is greater than 5 sec " + timeSinceLastAccess);
//                return -1;
//            }
//            return countMap.getOrDefault(key, -1);
//        }
//        finally {
//            lock.unlock();
//        }
//    }
//
//    public synchronized void put(int key, Object value) {
//        lock.lock();
//        try {
//            if (!valueMap.containsKey(key)) {
//                if (valueMap.size() == size) {
//                    synchronized (frequencyMap) {
//                        int lowestCount = frequencyMap.keySet().iterator().next();
//                        int keyToDelete = frequencyMap.get(lowestCount).remove(0);
//                        if (frequencyMap.get(lowestCount).isEmpty()) {
//                            frequencyMap.remove(lowestCount);
//                        }
//                        valueMap.remove(keyToDelete);
//                        countMap.remove(keyToDelete);
//                        System.out.println("Value removed from frequencyMap in if block" + valueMap.remove(keyToDelete));
//                    }
//                }
//                valueMap.put(key, value);
//                countMap.put(key, 1);
//                System.out.println("Value added to the Cache Service in if block " + valueMap.put(key, value));
//
//                synchronized (frequencyMap) {
//                    frequencyMap.computeIfAbsent(1, k -> new LinkedList<>()).add(key);
//                }
//            } else if (size < 100) {
//                valueMap.put(key, value);
//                System.out.println("Value added to the Cache Service in else if block " + valueMap.put(key, value));
//                int frequency = countMap.get(key);
//                synchronized (frequencyMap) {
//                    frequencyMap.get(frequency).remove(Integer.valueOf(key));
//                    if (frequencyMap.get(frequency).isEmpty()) {
//                        frequencyMap.remove(frequency);
//                        System.out.println("Value removed from map " + frequencyMap.remove(frequency));
//                    }
//                    frequencyMap.computeIfAbsent(frequency + 1, k -> new LinkedList<>()).add(key);
//                }
//                countMap.put(key, frequency + 1);
//                System.out.println("Value added to the Cache Service in else if block " + countMap.put(key, frequency + 1));
//            }
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    private void evict(int key) {
//        lock.lock();
//        try {
//            System.out.println("Evicting key " + key);
//            if (valueMap.containsKey(key)) {
//                valueMap.remove(key);
//                countMap.remove(key);
//                System.out.println("Removed key from cache: " + key);
//            } else {
//                System.out.println("Key not in cache: " + key);
//            }
//        } finally {
//            lock.unlock();
//        }
//    }
////
////    private int getLeastFrequentlyUsedKey() {
////        // used to keep track of the minimum count of a key in the cache, and it is set to Integer.MAX_VALUE
////        // to ensure that the minimum count is always greater than or equal to any count that we might subsequently encounter
////        int minCount = Integer.MAX_VALUE;
////        // to ensure that the method returns a valid key even if the cache is empty
////        // if the cache is empty, the loop in getLeastFrequentlyUsedKey() will not execute, and minKey will still have
////        // its initial value of -1. This means that the method will return -1 to indicate that no keys are present in the cache
////        int minKey = -1;
////
////        for (Map.Entry<Integer, Integer> entry : countMap.entrySet()) {
////            if (entry.getValue() < minCount) {
////                minCount = entry.getValue();
////                minKey = entry.getKey();
////            }
////        }
////        return minKey;
////    }
//}
