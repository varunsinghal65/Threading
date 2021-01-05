package com.varun.threading.challenges5.criticalSection1;

/**
 * Solution 2 :
 *
 * Use synchronised in a block instead of a method and syncrohonise it over a lock object.
 */
public class ResourceSharingThreadsSolutionTwo4 {

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
        Object lock = new Object();

        public void increment() {
            synchronized (this.lock) {
                this.items++;
            }
        }

        public void decrement() {
            /**
             * Assume decrementing thread was faster than incrementing and thus
             * when decrementing thread enters this method, it acquires "this.lock".
             * All the other threads will be denied access to blocks guarded by "this.lock" OR
             * synchonized over "this.lock"
             */
            synchronized (this.lock) {
                this.items--;
            }
        }

        public int getItems() {
            synchronized (this.lock) {
                return this.items;
            }
        }

    }

}
