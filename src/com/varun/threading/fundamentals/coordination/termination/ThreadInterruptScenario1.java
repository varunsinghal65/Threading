package com.varun.threading.fundamentals.coordination.termination;

/**
 * There are 2 scenarios, when we can use the interrupt() method on a thread.
 *
 * Scenario 1 :
 * if the the thread to be interrupted throws a InterruptedException.
 *
 * Logic :
 * When the interrupt method is called on such a thread, an interrupted exception is thrown, code in catch block is allowed
 * to terminate the thread or return the control.
 */
public class ThreadInterruptScenario1 {

    public static void main(String[] args) {
        Thread blockingThread = new Thread(new BlockingTask());
        blockingThread.start();
        blockingThread.interrupt();
    }

    private static class BlockingTask implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(5000000);
            } catch (InterruptedException e) {
                System.out.println("Blocking task interrupted externally");
            }
        }
    }
}
