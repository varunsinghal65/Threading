package com.varun.threading.fundamentals1.coordination2.termination1;

public class TerminatingThreadCorrectlyInCatch4 {

    public static void main(String [] args) {
        Thread thread = new Thread(new SleepingThread());
        thread.start();
        thread.interrupt();
    }

    private static class SleepingThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000000);
                } catch (InterruptedException e) {
                    // this statement is not sufficient. This will sure, break the sleep of thread
                    // but the thread will then again go to sleep because of the loop.
                    // NOTE : Catching an interrupted exception DOES NOT NECESSARILY MEAN THAT THREAD IS TERMINATED
                    // any code after catch block will still be executed by the thread.
                    // safest way is to return, which will truly terminate the thread.
                    // As a rule of thumb, never leave a catch block empty, and use the InterruptedException catch block to
                    // gracefully stop the current thread (by adding some print or cleaning code before returning from the run method)
                    System.out.println("Exception caught: " + e.getMessage());
                    return;
                }
            }
        }
    }

}
