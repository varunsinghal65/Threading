package com.varun.threading.advancedLocking;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * AIM : To show a practical use case, where reentrant lock is less performant.
 *
 * Use case :
 * Reentrant lock prevents concurrent access among writing threads and reading threads.
 * In fact, it prevent concurrent execution among reading threads as well.
 * Read operations are safe to be executed in a concurrent manner among themselves.
 * However, ReentrantLock will prevent this reducing app's performance.
 * This is particularly true, if the app has many read ops but less write ops.
 *
 * For e.g : A cache.
 *
 */
public class WhyReadWriteLockDImpl {

    public final static int HIGHEST_PRICE = 1000;

    public static void main(String[] args) throws InterruptedException {
        /**
         * DB storing the items belonging to a particular price.
         */
        InventoryDatabase db = new InventoryDatabase();
        Random random = new Random();

        /**
         * Populate the DB initially before the fun begins
         */
        for (int i = 0; i < 100000; i++) {
            db.addItem(random.nextInt(HIGHEST_PRICE));
        }

        /**
         * DB writing thread
         */
        Thread writerThread = new Thread(() -> {
            while (true) {
                db.addItem(random.nextInt(HIGHEST_PRICE));
                db.removeItem(random.nextInt(HIGHEST_PRICE));

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        writerThread.setDaemon(true);
        writerThread.start();

        /**
         * DB reading threads
         */
        int numberOfReadingThreads = 7;
        List<Thread> readingThreads = new ArrayList<>();
        for (int i = 0; i < numberOfReadingThreads; i++) {
            Thread readingThread = new Thread(()->{
                for (int j = 0; j<100000;j++) {
                    int upperBoundPrice = random.nextInt(HIGHEST_PRICE);
                    int lowerBoundPrice = upperBoundPrice > 0 ? random.nextInt(upperBoundPrice) : 0;
                    db.getItemsOfaPriceRange(lowerBoundPrice, upperBoundPrice);
                }
            });
            readingThreads.add(readingThread);
        }

        /**
         * Performance bench mark read operations without the ReentrantReadWriteLock
         */
        Instant start = Instant.now();
        for (Thread thread : readingThreads) {
            thread.start();
        }
        for (Thread thread : readingThreads) {
            thread.join();
        }
        Instant end = Instant.now();
        System.out.println(String.format("The read operations took %d milliseconds", Duration.between(start, end).toMillis()));
    }

    /**
     * MOCK db
     */
    private static class InventoryDatabase {
        /**
         * BST to mock DB and store the data
         */
        TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();
        ReentrantLock dbLock = new ReentrantLock();

        public int getItemsOfaPriceRange(int lowerBoundPrice, int upperBoundPrice) {
            /**
             * This lock will allow execution of this critical section only by 1 thread at a time.
             */
            dbLock.lock();
            try {
                Integer fromKey = priceToCountMap.ceilingKey(lowerBoundPrice);
                Integer toKey = priceToCountMap.floorKey(upperBoundPrice);

                if (fromKey == null || toKey == null) {
                    return 0;
                }

                NavigableMap<Integer, Integer> candidateMap = priceToCountMap.subMap(fromKey, true, toKey, true);

                int sum = 0;
                for (int itemsOfGivenPrice : candidateMap.values()) {
                    sum = sum + itemsOfGivenPrice;
                }
                return sum;
            } finally {
                dbLock.unlock();
            }
        }

        public void addItem(int price) {
            dbLock.lock();
            try {
                Integer itemsOfPrice = priceToCountMap.get(price);
                if (itemsOfPrice == null) {
                    priceToCountMap.put(price, 1);
                } else {
                    priceToCountMap.put(price, itemsOfPrice + 1);
                }
            } finally {
                dbLock.unlock();
            }

        }

        public void removeItem(int price) {
            dbLock.lock();
            try {
                Integer itemsOfPrice = priceToCountMap.get(price);
                if (itemsOfPrice == null || itemsOfPrice == 0) {
                    priceToCountMap.remove(price);
                } else {
                    priceToCountMap.put(price, itemsOfPrice - 1);
                }
            } finally {
                dbLock.unlock();
            }
        }
    }
}
