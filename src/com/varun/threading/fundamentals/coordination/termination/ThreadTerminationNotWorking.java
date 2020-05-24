package com.varun.threading.fundamentals.coordination.termination;

/**
 * The interrupt() method of thread class is used to interrupt the thread.
 * If any thread is in sleeping or waiting state (i.e. sleep() or wait() is invoked) then using the interrupt() method,
 * we can interrupt the thread execution by throwing InterruptedException.
 * <p>
 * If the thread is not in the sleeping or waiting state then calling the interrupt()
 * method performs a normal behavior and doesn't interrupt the thread but sets the interrupt flag to true.
 */
public class ThreadTerminationNotWorking {

    public static void main(String[] args) {
        BlockingThread t = new BlockingThread();
        t.start();
        t.interrupt();
    }

    private static class BlockingThread extends Thread {
        @Override
        public void run() {
            try {
                executeInfiniteLoop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void executeInfiniteLoop() throws InterruptedException {
            while (true) {
                System.out.println("Infinite loop is running");
            }
        }
    }
}
