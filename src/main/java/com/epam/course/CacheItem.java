package com.epam.course;

public class CacheItem<T> {

    public T item;
    public Long timestamp;

    public CacheItem(T item, Long timestamp) {
        this.item = item;
        this.timestamp = timestamp;
    }
}