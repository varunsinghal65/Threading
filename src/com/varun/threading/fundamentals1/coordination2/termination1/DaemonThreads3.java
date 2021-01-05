package com.varun.threading.fundamentals1.coordination2.termination1;

import java.math.BigInteger;
/**
 * Problem:
 * Imagine that LongComputationTask is using a external library for calculation.
 * The external library does not handle interrupt signal explicitly.
 * So, interrupt() would be useless.
 * And, longComputationThread will prevent our application to be terminated, even if main thread is done.
 *
 * Solution:
 * Make the longComputationThread as a daemon thread, so our application can exit without caring if longComputationThread has completed or not.
 * longComputationThread would run in background.
 *
 * NOTE: setDaemon(true) should be called before the thread.start()
 *
 */
public class DaemonThreads3 {

    public static void main(String[] args) {

        Thread longComputationThread = new Thread(new LongComputationTask(new BigInteger("20000"), new BigInteger("1000000")));
        // Remove this and the thread will prevent the app's termination
        longComputationThread.setDaemon(true);
        longComputationThread.start();
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
                result = result.multiply(base);
            }
            return result;
        }
    }
}
