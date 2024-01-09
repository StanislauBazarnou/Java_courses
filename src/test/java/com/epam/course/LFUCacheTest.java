package com.epam.course;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class LFUCacheTest {

    static int DEFAULT_CAPACITY = 4;
    static int DEFAULT_TTL = 15;

    /**
     * Insert more than 100 items and then check that the size of the cache is indeed 100 due to eviction,
     * and not more than 100
     */
    @Test
    void testCacheSize() {
        // Given
        int cacheSize = 100;
        LFUCache<Integer, String> cache = new LFUCache<>(cacheSize, 10);

        // When insert 110 items
        for(int i = 0; i < 110; i++) {
            cache.put(i, "Value " + i);
        }

        // Then check the size is within the limit of 100
        assertEquals(cacheSize, cache.getCache().size());
        // Verify that the first 10 items were evicted
        for(int i = 0; i < 10; i++) {
            assertNull(cache.get(i));
        }
        // Verify that the last 100 items are still in the cache
        for(int i = 10; i < 110; i++) {
            assertEquals("Value " + i, cache.get(i));
        }
    }

    /**
     * Verify that one of the items from 1 to 4, which were less frequently accessed, was evicted
     */
    @Test
    void testLFUEviction() {
        // Given
        int cacheSize = 5;
        LFUCache<Integer, String> cache = new LFUCache<>(cacheSize, 10);

        // When insert 5 items
        for(int i = 0; i < cacheSize; i++) {
            cache.put(i, "Value " + i);
        }
        // Access some of the items to change their frequency
        for(int i = 0; i < cacheSize; i++) {
            cache.get(i);
        }
        // Access an item again, so now the least frequently used items should be any of 1-4
        cache.get(0);
        // Now add a sixth item. This should evict one of the less frequently used items (1, 2, 3, or 4)
        cache.put(cacheSize, "Value " + cacheSize);

        // Then verify cache size
        assertEquals(cacheSize, cache.getCache().size());
        // Verify the least frequently used item was evicted
        assertNotNull(cache.get(0)); // The frequently accessed item should still be present
        assertNotNull(cache.get(5)); // The recently added item should be present
        int nullCount = 0;
        for(int i = 1; i < cacheSize; i++) {
            if(cache.get(i) == null) nullCount++;
        }
        assertEquals(1, nullCount); // One of the items from 1 to 4 should have been evicted
    }

    /**
     * Add an item to the cache, then get it several times and ensure that the frequency is being correctly tracked
     */
    @Test
    void testItemFrequency() {
        // given
        LFUCache<Integer, String> cache = new LFUCache<>(10, 2);
        Integer testKey = 1;
        String testValue = "Test Value";
        // when
        cache.put(testKey, testValue);
        for (int i = 0; i < 5; i++) {
            cache.get(testKey);
        }
        // Then check if the frequency of the test item is correctly tracked
        assertEquals(5, cache.getCountOfGetsMap().get(testKey).intValue());
    }

    /**
     * Verify if the eviction statistics are properly updated
     */
    @Test
    void testCacheEvictionsStatistics() {
        // Given
        int cacheSize = 5;
        LFUCache<Integer, String> cache = new LFUCache<>(cacheSize, 10);

        // When insert cacheSize+2 items. This will cause two items eventually to be evicted
        for(int i = 0; i < cacheSize + 2; i++) {
            cache.put(i, "Value " + i);
        }

        // Then Check eviction statistics
        assertEquals(2, cache.statistics.getNumberOfCacheEvictions());
    }

    @Test
    void updateExistingValueTest() {
        // given
        LFUCache<Integer, String> cache = new LFUCache<>(DEFAULT_CAPACITY, DEFAULT_TTL);

        Integer key = 1;
        String initialValue = "A";
        String updatedValue = "B";
        int countOfGets = 5;

        // when
        cache.put(key, initialValue);
        for (int i = 0; i < countOfGets; i++) {
            cache.get(key);
        }
        cache.put(key, updatedValue);

        // then
        assertEquals(
                countOfGets,
                cache.getCountOfGetsMap().get(key)
        );

        // show statistics
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            cache.getAverageTimeSpent();
            cache.showStatistics();
        }));
    }

    /**
     * Capacity: 4
     * K V -> getCount
     * ================
     * 1 A -> 4
     * 2 B -> 10
     * 3 C -> 3
     * 4 D -> 5
     * frequencyMap
     * 0 1 2 3 4 5 6 7 8 9 10
     * X X X 3 A D X X X X B
     * 3 A D         B
     * put(5, E);
     * K V -> getCount
     * ================
     * 1 A -> 4
     * 2 B -> 10
     * delete[3 C -> 3]
     * 4 D -> 5
     * 5 E -> 0
     */
    @Test
    @DisplayName("Check if eviction policy would remove the least frequently accessed element")
    void evictionTest() {
        // given
        int capacity = 4;
        LFUCache<Integer, String> cache = new LFUCache<>(capacity, DEFAULT_TTL);
        int leastUsedElementKey = 3;
        int leastUsedElementAccessCount = 3;

        // when
        cache.put(1, "A");
        cache.put(2, "B");
        cache.put(leastUsedElementKey, "C");
        cache.put(4, "D");

        for (int i = 0; i < leastUsedElementAccessCount + 1; i++) {
            cache.get(1);
        }
        for (int i = 0; i < leastUsedElementAccessCount + 7; i++) {
            cache.get(2);
        }
        for (int i = 0; i < leastUsedElementAccessCount; i++) {
            cache.get(leastUsedElementKey);
        }
        for (int i = 0; i < leastUsedElementAccessCount + 2; i++) {
            cache.get(4);
        }

        cache.put(5, "E");

        // then
        assertEquals(capacity, cache.getCache().size());
        assertNull(cache.get(leastUsedElementKey));

        // show statistics
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            cache.getAverageTimeSpent();
            cache.showStatistics();
        }));
    }

    @Test
    void usageTest() throws InterruptedException {
        // given
        LFUCache<Integer, Integer> cache = new LFUCache<>(100, DEFAULT_TTL);
        Statistics statistics = new Statistics();

        // when
        Thread producer = new Thread(() -> {
            for (int i = 1; i < 10; i++) {
                cache.put(i, i);
                log.info("Value is added to cache: {}", i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        producer.start();

        int numberOfConsumers = 30;
//        ExecutorService executorService = Executors.newFixedThreadPool(numberOfConsumers); // 100 пример с Дорожек
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(numberOfConsumers); // 100 пример с Дорожек
        for (int consumerIndex = 0; consumerIndex < numberOfConsumers; consumerIndex++) {
            executorService.schedule(() -> {
                for (int i = 1; i < 10; i++) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Integer value = cache.get(i);
                    log.info("Value is read from cache: {}", value);
                }
            }, (consumerIndex/3)*3, TimeUnit.SECONDS);
        }
        executorService.awaitTermination(10L, TimeUnit.SECONDS);

        // show statistics
        // Runtime.getRuntime().addShutdownHook(new Thread(statistics::getAverageTimeSpent));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            cache.getAverageTimeSpent();
            cache.showStatistics();
        }));
    }

    // then
//    assertEquals(100,cache.getCountOfGetsMap().
//
//    get(1));
    // TODO Add assertions


    @Test
    void timeBasedOnLastAccessTest() {
        // given
        int capacity = 5;
        int ttlInSeconds = 1;
        LFUCache<Integer, String> cache = new LFUCache<>(capacity, ttlInSeconds);

        // when
        cache.put(1, "A");
        cache.put(2, "B");
        cache.put(3, "C");

        int timeToWaitInSeconds = ttlInSeconds + 1;
        try {
            Thread.sleep(timeToWaitInSeconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // then
        assertNull(cache.get(1));
        assertNull(cache.get(2));
        assertNull(cache.get(3));
    }

    /**
     In this test, we start 10 threads which each put a value into the cache and then retrieve it.
     The assertion checks that the retrieved value matches the expected value. Since the cache put
     and get operations are run in separate threads, this test checks the cache's correctness under
     concurrent access
     */
    @Test
    void testConcurrentAccess() throws InterruptedException {
        LFUCache<Integer, String> cache = new LFUCache<>(3, 5);

        List<Thread> threads = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            int finalI = i;
            Thread thread = new Thread(() -> {
                cache.put(finalI, "Value " + finalI);
                String value = cache.get(finalI);
                assertEquals("Value " + finalI, value);
            });
            threads.add(thread);
            thread.start();
        }

        for(Thread thread : threads) {
            thread.join();
        }
    }
}
