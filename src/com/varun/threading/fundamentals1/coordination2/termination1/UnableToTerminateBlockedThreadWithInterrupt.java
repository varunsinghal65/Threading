package com.varun.threading.fundamentals1.coordination2.termination1;

/**
 * Lesson learnt,
 *
 * Interrupt call to a thread will only terminate the thread, if the thread's state is WAITING.
 * If any other state, thread will continue to be in that state and only the isInterrupted flag will be set as true.
 */
public class UnableToTerminateBlockedThreadWithInterrupt {

    public static void main(String[] args) throws InterruptedException {
        SharedClass sharedInstance = new SharedClass();

        Thread competingThread1 = new Thread(()->{
            try {
                sharedInstance.incrementSharedCounter();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        competingThread1.setName("competingThread1");

        Thread competingThread2 = new Thread(()->{
            try {
                sharedInstance.incrementSharedCounter();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        competingThread2.setName("competingThread2");

        Thread waitingThread = new Thread(()->{
            try {
                Thread.sleep(1000000000);
            } catch (InterruptedException e) {
                System.out.println("Waiting Thread interrupted successfully");
            }
        });

        waitingThread.start();
        competingThread1.start();
        //Allow thread 1 to acquire lock
        Thread.sleep(2000);
        competingThread2.start();
        //Allow thread 2 to get BLOCKED
        Thread.sleep(2000);

        //At his point, competingThread1: RUNNABLE, competingThread2: BLOCKED, waitingThread: WAITING
        //Interrupt a BLOCKED state thread, whose code is throwing a InterruptedException
        System.out.println("competingThread2 state before interruption: " + competingThread2.getState());
        System.out.println("competingThread2 interrupted flag before interruption: " + competingThread2.isInterrupted());
        competingThread2.interrupt();
        //Allow time for the signal to reach the thread to be interrupted
        Thread.sleep(1000);
        // STATE of competingThread2 will still be BLOCKED, and not TERMINATED, since we tried interrupting
        // a BLOCKED state thread
        System.out.println("competingThread2 state after interruption: " + competingThread2.getState());
        System.out.println("competingThread2 interrupted flag after interruption: " + competingThread2.isInterrupted());

        //Interrupt a RUNNABLE state thread, whose code is throwing a InterruptedException
        System.out.println("competingThread1 state before interruption: " + competingThread1.getState());
        System.out.println("competingThread1 interrupted flag before interruption: " + competingThread1.isInterrupted());
        competingThread1.interrupt();
        Thread.sleep(1000);
        // STATE of competingThread2 will still be RUNNABLE, and not TERMINATED, since we tried interrupting
        // a RUNNABLE state thread
        System.out.println("competingThread1 state after interruption: " + competingThread1.getState());
        System.out.println("competingThread1 interrupted flag after interruption: " + competingThread1.isInterrupted());

        //Interrupt a WAITING state thread, whose code is throwing a InterruptedException
        System.out.println("waitingThread state before interruption: " + waitingThread.getState());
        System.out.println("waitingThread interrupted flag before interruption: " + waitingThread.isInterrupted());
        waitingThread.interrupt();
        Thread.sleep(1000);
        // STATE of competingThread2 will still be RUNNABLE, and not TERMINATED, since we tried interrupting
        // a RUNNABLE state thread
        System.out.println("waitingThread state after interruption: " + waitingThread.getState());
        System.out.println("waitingThread interrupted flag after interruption: " + waitingThread.isInterrupted());

    }
    private static class SharedClass{
        private int sharedCounter = 0;

        public synchronized void incrementSharedCounter() throws InterruptedException {
            while(true) {
                //System.out.println("Shared counter value is" + sharedCounter +  ", being incremented by thread" + Thread.currentThread().getName());
                sharedCounter++;
            }
        }
    }

}
