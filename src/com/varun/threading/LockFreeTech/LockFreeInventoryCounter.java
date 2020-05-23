package com.varun.threading.LockFreeTech;

import java.util.concurrent.atomic.AtomicInteger;

public class LockFreeInventoryCounter {

    public static void main(String[] args) throws InterruptedException {
        InventoryCounter ic = new InventoryCounter();
        Thread incrementingThread = new Thread(()->{
            for (int i =0;i<100000;i++) {
                ic.increment();
            }
        });
        Thread decrementingThread = new Thread(()->{
            for (int i =0;i<100000;i++) {
                ic.decrement();
            }
        });
        incrementingThread.start();
        decrementingThread.start();
        incrementingThread.join();
        decrementingThread.join();

        System.out.println(ic.getCounter());
    }

    private static class InventoryCounter {
        private AtomicInteger counter = new AtomicInteger(0);
        public void increment() {
            this.counter.incrementAndGet();
        }
        public void decrement() {
            this.counter.decrementAndGet();
        }
        public int getCounter() {
            return this.counter.get();
        }
    }

}
