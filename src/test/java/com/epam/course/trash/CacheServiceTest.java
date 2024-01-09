package com.epam.course.trash;//import org.example.CacheService;
//
//public class CacheServiceTest {
//    @Test
//    @DisplayName("Testing removal of entries based on TTL")
//    void expiryTest() throws InterruptedException {
//        CacheService cacheService = new CacheService(5, EvictionPolicy.LEAST_RECENTLY_USED);
//
//        CacheService.Key key = new CacheService.Key();
//        key.id = "OurFirstEntry";
//
//        CacheService.Entry entry = new CacheService.Entry();
//        entry.name = "MyName";
//
//        cacheService.put(key, entry);
//        System.out.printf("Added value: %s, timestamp: %d\n", cacheService.get(key).name, cacheService.get(key).putTimestamp);
//
//        Thread.sleep(6000L);
//
//        assertNull(cacheService.get(key));
//    }
//
//    @Test
//    @DisplayName("Eviction policy: LRU")
//    void cacheLRUTest() throws InterruptedException {
//        CacheService cacheService = new CacheService(5, EvictionPolicy.LEAST_RECENTLY_USED);
//
//        CacheService.Key key1 = new CacheService.Key();
//        key1.id = "OurFirstEntry";
//
//        CacheService.Entry entry1 = new CacheService.Entry();
//        entry1.name = "OurFirstEntry";
//
//        CacheService.Key key2 = new CacheService.Key();
//        key2.id = "OurSecondEntry";
//
//        CacheService.Entry entry2 = new CacheService.Entry();
//        entry2.name = "OurSecondEntry";
//
//        CacheService.Key key3 = new CacheService.Key();
//        key3.id = "OurThirdEntry";
//
//        CacheService.Entry entry3 = new CacheService.Entry();
//        entry3.name = "OurThirdEntry";
//
//        // Eviction policy test
//        cacheService.put(key1, entry1);
//        cacheService.put(key2, entry2);
//
//        Thread.sleep(5000L);
//        cacheService.get(key1);
//
//        cacheService.put(key3, entry3);
//
//
//        System.out.println("after adding 3 key");
//        System.out.println("key1: " + cacheService.get(key1));
//        System.out.println("key2: " + cacheService.get(key2));
//        System.out.println("key3: " + cacheService.get(key3));
//
//        System.out.println("after TTL");
//        Thread.sleep(6000L);
//        System.out.println("key1: " + cacheService.get(key1));
//        System.out.println("key2: " + cacheService.get(key2));
//        System.out.println("key3: " + cacheService.get(key3));
//    }
//
//    @Test
//    @DisplayName("Eviction policy: LFU")
//    void cacheLFUTest() {
//        CacheService cacheService = new CacheService(5, EvictionPolicy.LEAST_RECENTLY_USED);
//
//    }
//
//    @Test
//    @DisplayName("Statistics")
//    void cacheStatisticsTest() {
//        CacheService cacheService = new CacheService(5, EvictionPolicy.LEAST_RECENTLY_USED);
//
//    }
//
//    @Test
//    @DisplayName("Concurrency test")
//    void cacheConcurrencyTest() {
//        CacheService cacheService = new CacheService(5, EvictionPolicy.LEAST_RECENTLY_USED);
//
//
//        Thread user1 = new Thread(() ->
//                cacheService.put(
//                        new CacheService.Key(),
//                        new CacheService.Entry())
//        );
//        user1.start();
//
//        Thread user2 = new Thread(() ->
//                cacheService.put(
//                        new CacheService.Key(),
//                        new CacheService.Entry())
//        );
//        user2.start();
//    }
//}
