package com.varun.threading.InterThreadComm;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Problem statement :
 * <p>
 * Their is a buffer, producer threads adds items to that buffer and consumer threads removes items.
 * <p>
 * Implement this buffer, so that :
 * <p>
 * 1. addition is done only when buffer has space
 * 2. removal is done only when there is something to remove
 * 3. if the producer is very fast as compared to consumer, back pressure should be applied to prevent out of memory error.
 * <p>
 * SOLUTION :
 * https://javarevisited.blogspot.com/2015/07/how-to-use-wait-notify-and-notifyall-in.html#axzz6m5iZu92eS
 */
public class ProducerConsumer {
    public static void main(String[] args) {
        final int PRODUCER_THREAD_NO = 50;
        final int CONSUMER_THREAD_NO = 1;

        //ThreadSafeQueueUsingWaitNotify<Integer> q = new ThreadSafeQueueUsingWaitNotify<>(5);
        ThreadSafeQueueUsingSemaphore<Integer> q = new ThreadSafeQueueUsingSemaphore<>(5);
        List<Thread> producerThreads = new ArrayList<>();
        List<Thread> consumerThreads = new ArrayList<>();

        Runnable producerTask = () -> {
            while (true) {
                try {
                    q.add(new Random().nextInt());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable consumerTask = () -> {
            while (true) {
                try {
                    q.remove();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        for (int i = 0; i < PRODUCER_THREAD_NO; i++) {
            producerThreads.add(new Thread(producerTask));
        }
        for (int i = 0; i < CONSUMER_THREAD_NO; i++) {
            consumerThreads.add(new Thread(consumerTask));
        }

        Thread monitoringThread = new Thread(() -> {
            while (true) {
                q.printBufferSize();
            }
        });

        producerThreads.forEach(Thread::start);
        consumerThreads.forEach(Thread::start);
        monitoringThread.start();
    }

    private static class ThreadSafeQueueUsingWaitNotify<T> {
        private int maxBufferSize;
        private Queue<T> buffer;

        ThreadSafeQueueUsingWaitNotify(int maxBufferSize) {
            this.maxBufferSize = maxBufferSize;
            buffer = new LinkedList<>();
        }

        public void add(T data) throws InterruptedException {
            synchronized (this) {
                while (true) {
                    if (buffer.size() == maxBufferSize) {
                        System.out.println("Producer: waiting for consumer to consume");
                        wait();
                    } else {
                        break;
                    }
                }
                buffer.add(data);
                notifyAll();
            }
        }

        public T remove() throws InterruptedException {
            synchronized (this) {
                while (true) {
                    if (buffer.isEmpty()) {
                        System.out.println("Consumer: waiting for producer to produce");
                        wait();
                    } else {
                        break;
                    }
                }
                Thread.sleep(1000);
                T toReturn = buffer.remove();
                notifyAll();
                return toReturn;
            }
        }

        public void printBufferSize() {
            synchronized (this) {
                System.out.println(buffer.size());
            }
        }
    }

    private static class ThreadSafeQueueUsingSemaphore<T> {
        private int maxBufferSize;
        private final Queue<T> buffer;

        private final ReentrantLock lock;
        private final Semaphore productionAllowed;
        private final Semaphore consumptionAllowed;

        ThreadSafeQueueUsingSemaphore(int maxBufferSize) {
            this.maxBufferSize = maxBufferSize;
            buffer = new LinkedList<>();
            lock = new ReentrantLock();
            productionAllowed = new Semaphore(maxBufferSize);
            consumptionAllowed = new Semaphore(0);
        }

        public void add(T data) throws InterruptedException {
            productionAllowed.acquire();
            try {
                lock.lock();
                buffer.add(data);
            } finally {
                lock.unlock();
            }
            consumptionAllowed.release();
        }

        public T remove() throws InterruptedException {
            T toReturn = null;
            consumptionAllowed.acquire();
            try {
                lock.lock();
                toReturn = buffer.remove();
            } finally {
                lock.unlock();
            }
            productionAllowed.release();
            return toReturn;
        }

        public void printBufferSize() {
            try {
                lock.lock();
                System.out.println(buffer.size());
            } finally {
                lock.unlock();
            }
        }
    }
}
