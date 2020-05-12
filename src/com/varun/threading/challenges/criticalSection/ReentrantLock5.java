package com.varun.threading.challenges.criticalSection;

/**
 * Synchronized is a reentrant block.
 * <p>
 * Meaning once a thread enter such a block, it acquires the lock that was guarding that sync block and now the thread can
 * execute any sync method calls within that method, that are also guarded by the lock that it acquired.
 */
public class ReentrantLock5 {

    public static void main(String[] args) throws InterruptedException {
        // heap shared, all class variables shared including the items
        InventoryCounter ic = new InventoryCounter();
        IncrementingThread iT = new IncrementingThread(ic);
        DecrementingThread dT = new DecrementingThread(ic);

        iT.start();
        dT.start();
        //at this point iT and dT are running at the same time by OS.

        // allow iT to finish, not dT is also running
        iT.join();
        // allow dT to finish
        dT.join();

        // Every time you will get a different result ever single time, none of which is right.
        System.out.println("We have currently " + ic.getItems() + " items");

    }

    private static class IncrementingThread extends Thread {
        private InventoryCounter iCounter;

        public IncrementingThread(InventoryCounter ic) {
            this.iCounter = ic;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                this.iCounter.increment();
            }
        }
    }

    private static class DecrementingThread extends Thread {
        private InventoryCounter iCounter;

        public DecrementingThread(InventoryCounter ic) {
            this.iCounter = ic;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                this.iCounter.decrement();
            }
        }
    }

    private static class InventoryCounter {
        private int items = 0;

        public void increment() {
            synchronized (this) {
                this.items++;
                // incrementing thread can access doNothing, since its guarded by the same lock that it has already accquired.
                doNothingIncrementing();
            }
        }

        public void doNothingIncrementing() {
            synchronized (this) {
                // do nothing
            }
        }

        public synchronized void decrement() {
            this.items--;
            // the decrementing thread will eventually enter this acquiring lock on "this".
            // all the other threads will be denied access to sync methods that are locked by lock : "this".
            // However, decrementing thread can easily call one asynch method (guarded by lock : "this")
            // from another asynch method guarded with the same lock.
            // thus synchronised is also called reentrant lock
            doNothingDecrementing();
        }

        public synchronized void doNothingDecrementing() {
            synchronized (this) {
                // do nothing
            }
        }

        public synchronized int getItems() {
            return this.items;
        }

    }

}
