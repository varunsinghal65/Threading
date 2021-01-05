package com.varun.threading.challenges5.criticalSection1;

/**
 * Solution 1
 *
 * Make the critical section content as atomic by gaurding it with a synchronised keyword.
 *
 * Synchronised applies at object level, meaning, if one synchronised marked method is being xecuted by thread X, thread Y will be denied access to all
 * the synchronised marked methods --> this is called monitor.
 *
 */
public class ResourceSharingThreadsSolutionOne3 {

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

        public synchronized void increment() {
            // make the content of critical section as atomic.
            // this is called critical section as this op consists of 3 sub-operations.
            // in our app, these sub operations and the one in decrement method can be called concurrently corrupting
            // the intention of the increment and decrement methods.
            // synchronised ensures that only 1 threads can modify "items" while other waits for it's turn.
            this.items++;
        }

        public synchronized void decrement() {
            this.items--;
        }

        public int getItems() {
            return this.items;
        }

    }

}
