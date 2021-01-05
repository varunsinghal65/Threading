package com.varun.threading;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Requirments
 *
 * mutex between producer and consumer
 * producer should produce when queeu has space
 * consumer should consume when producer has produced.
 */

class practice2 {

    public static void main(String[] args) {
        final int NO_OF_PRODUCERS = 20;
        final int NO_OF_CONSUMERS = 1;

        List<Thread> producers = new ArrayList<>();
        List<Thread> consumers = new ArrayList<>();

        ThreadSafeQueue<Employee> empsQueue = new ThreadSafeQueue<>();
        Random rnd = new Random();

        for (int i=0;i<NO_OF_PRODUCERS;i++) {
            Thread producer = new Thread(()->{
                while(true) {
                    try {
                        empsQueue.enqueque(new Employee("Alpha", new BigDecimal(Integer.toString(rnd.nextInt()))));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            producers.add(producer);
        }

        for (int i=0;i<NO_OF_CONSUMERS;i++) {
            Thread consumer = new Thread(()->{
                while(true) {
                    try {
                        empsQueue.dequeue();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            consumers.add(consumer);
        }

        producers.forEach(Thread::start);
        consumers.forEach(Thread::start);

        Thread queueSizeMonitor = new Thread(()->{
           while(true) {
               System.out.println("Number of elements in the queue: " + empsQueue.getSize());
           }
        });

        queueSizeMonitor.start();
    }

    private static class Employee {
        String name;
        BigDecimal salary;
        Employee(String name, BigDecimal salary) {
            this.name = name;
            this.salary = salary;
        }
    }

    private static class ThreadSafeQueue<T> {
        private static final int QUEUE_CAP = 5;
        private final Queue<T> internalQueue = new ArrayDeque<>();
        Semaphore emptySem = new Semaphore(QUEUE_CAP);
        Semaphore fullSem = new Semaphore(0);
        Lock internalQueueLock = new ReentrantLock();
        public void enqueque(T item) throws InterruptedException {
            emptySem.acquire();
            internalQueueLock.lock();
            internalQueue.add(item);
            internalQueueLock.unlock();
            fullSem.release();
        }
        public T dequeue() throws InterruptedException {
            T toReturn = null;
            fullSem.acquire();
            internalQueueLock.lock();
            toReturn = internalQueue.remove();
            internalQueueLock.unlock();
            emptySem.release();
            return toReturn;
        }
        public int getSize() {
            int toReturn;
            internalQueueLock.lock();
            toReturn = internalQueue.size();
            internalQueueLock.unlock();
            return toReturn;
        }
    }
}