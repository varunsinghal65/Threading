package com.varun.threading.challenges.criticalSection;

public class ResourceSharingThreadsProblem2 {

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
            // this is a critical section -- meaning the content of this method is not an atomic operation.
            this.items++;
        }

        public void decrement() {
            this.items--;
        }

        public int getItems() {
            return this.items;
        }

    }

}
