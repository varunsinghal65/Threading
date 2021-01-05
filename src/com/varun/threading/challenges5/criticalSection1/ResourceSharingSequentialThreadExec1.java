package com.varun.threading.challenges5.criticalSection1;

public class ResourceSharingSequentialThreadExec1 {

    public static void main(String[] args) throws InterruptedException {
        // heap shared, all class variables shared including the items
        InventoryCounter ic = new InventoryCounter();
        IncrementingThread iT = new IncrementingThread(ic);
        DecrementingThread dT = new DecrementingThread(ic);

        iT.start();
        //wait for op to finish
        iT.join();
        dT.start();
        //wait for op to finish
        dT.join();

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
