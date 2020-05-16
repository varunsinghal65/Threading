package com.varun.threading.advancedLocking;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
 * Solution :
 * Use ReentrantReadWriteLock, this lock prevent concurrent access between read and write operations but allows concurrent execution among reader threads.
 * There by making our app 3 times faster.
 *
 */
public class ReadWriteLockDBImpl {

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
         * Performance bench mark read operations with the ReentrantReadWriteLock
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
        private TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();
        private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        private Lock readLock = rwLock.readLock();
        private Lock writeLock = rwLock.writeLock();

        public int getItemsOfaPriceRange(int lowerBoundPrice, int upperBoundPrice) {
            /**
             * Since this a read operation, i will use read lock, this lock will allow all reading threads to execute this method concurrently
             */
            readLock.lock();
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
                readLock.unlock();
            }
        }

        public void addItem(int price) {
            /**
             * Since this a write operation, i will use write lock, this lock will allow only 1 thread to execute critical section at a time.
             */
            writeLock.lock();
            try {
                Integer itemsOfPrice = priceToCountMap.get(price);
                if (itemsOfPrice == null) {
                    priceToCountMap.put(price, 1);
                } else {
                    priceToCountMap.put(price, itemsOfPrice + 1);
                }
            } finally {
                writeLock.unlock();
            }

        }

        public void removeItem(int price) {
            writeLock.lock();
            try {
                Integer itemsOfPrice = priceToCountMap.get(price);
                if (itemsOfPrice == null || itemsOfPrice == 0) {
                    priceToCountMap.remove(price);
                } else {
                    priceToCountMap.put(price, itemsOfPrice - 1);
                }
            } finally {
                writeLock.unlock();
            }
        }
    }
}
