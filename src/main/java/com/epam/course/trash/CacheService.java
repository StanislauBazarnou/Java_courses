package com.epam.course.trash;//package org.example;
//
//public class CacheService {
//    // LFU - last frequently used - here can be implementation of CacheService
//    // LRU - last recently used
//
//    // create constructor and fill concurrent map
//    // Map<String, CacheItem>
//    // add to constructor TimeAccessor (C#) - because of Instant.now().getEpochSecond()
//
//    void addItem(String key, Object value){
//        // Map.add key = key, value = CacheItem
//        // var item = new CacheItem(value, Instant.now().getEpochSecond());
//        // Map.add(key, item);
//
//        // tryToAdd or addOrUpdate - how to handle duplicated elements (maybe just update lastAccessTime)
//        // or throw exception - this element already exists in cache
//    }
//
//    Object getItem(String key) {
//        // if element not found
//        // var item = Map.get(key)
//        // update lastAccessTime if element accessed - update timeStamp field
//
//        // return item.value();
//    }
//
//    Map<String, CacheItem> removeItem(String key) {
//        // Map.remove(key)
//        //
//    }
//
//    void removeLastFrequentlyUsedItem() {
//        // check lastAccessTime
//        // removeItem()
//    }
//}
