package com.varun.threading.fundamentals1.coordination2.termination1;

import java.math.BigInteger;
/**
 * There are 2 scenarios, when we can use the interrupt() method on a thread.
 *
 * Scenario 2 :
 * if the the code of the thread to be interrupted, is handling the interrupt signal explicitly.
 *
 */
public class ThreadInterruptScenario2 {

    public static void main(String[] args) {

        Thread longComputationThread = new Thread(new LongComputationTask(new BigInteger("20000"), new BigInteger("1000000")));
        longComputationThread.start();

        // scenario 1, thread code is not handling the interrupt signal explicitly and not throwing a interrupted exception
        // This will have no impact because, thread is not throwing a InterruptedException

        // scenario 2, thread code is handling the interrupt signal explicitly and not throwing a interrupted exception
        // This will have an impact because, thread code is constantly listening for the interrupt signal.
        // remove below statement and this thread will take long time
        longComputationThread.interrupt();

    }

    private static class LongComputationTask implements Runnable {
        private BigInteger base = BigInteger.ONE;
        private BigInteger pow = BigInteger.ONE;

        public LongComputationTask(BigInteger base, BigInteger pow) {
            this.base = base;
            this.pow = pow;
        }

        @Override
        public void run() {
            System.out.println(base + "^" + pow + "=" + pow(this.base, this.pow));
        }

        private Object pow(BigInteger base, BigInteger pow) {
            BigInteger result = BigInteger.ONE;
            for (BigInteger i = BigInteger.ZERO; i.compareTo(this.pow) != 0; i = i.add(BigInteger.ONE)) {
                //Check if this long computation thread was interrupted from outside world
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Thread interrupted pre-maturely");
                    System.exit(0);
                }
                result = result.multiply(base);
            }
            return result;
        }
    }
}
