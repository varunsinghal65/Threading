package com.varun.threading.challenges.VolatileExample;

import java.time.Duration;
import java.time.Instant;

public class MinMaxMetricMeasurement {

    public static void main(String[] args) throws InterruptedException {
        MinMaxMetrics minMaxM = new MinMaxMetrics();
        MinMaxPrinterThread printer = new MinMaxPrinterThread(minMaxM);
        BusinessThread bt1 = new BusinessThread(minMaxM, 3000);
        BusinessThread bt2 = new BusinessThread(minMaxM, 56);
        BusinessThread bt3 = new BusinessThread(minMaxM, 1000);
        BusinessThread bt4 = new BusinessThread(minMaxM, 500);
        BusinessThread bt5 = new BusinessThread(minMaxM, 8);


        bt1.start();
        bt2.start();
        bt3.start();
        bt4.start();
        bt5.start();

        // wait for business threads to finish
        bt1.join(5000);
        bt2.join(5000);
        bt3.join(5000);
        bt4.join(5000);
        bt5.join(5000);

        // schedule printer thread
        // note : Its very difficult to demonstrate use of volatile, however, idea is if multiple threads call read a variable of type long/double, read is not atomic.
        // as a result, due to concurrent execution of the 2 threads during read, the 2 operations might be mixed in each other thread's execution
        // corrupting the result.
        // Always, if a long/double variable shared across threads has to be read, declare it volatile
        printer.start();
    }

    private static class BusinessThread extends Thread {
        private MinMaxMetrics metrics;
        private long mockedTimeForSvcExec;

        public BusinessThread(MinMaxMetrics metrics, long mockedTimeForSvcExec) {
            this.metrics = metrics;
            this.mockedTimeForSvcExec = mockedTimeForSvcExec;
        }

        @Override
        public void run() {
            Instant start = Instant.now();
            try {
                Thread.sleep(mockedTimeForSvcExec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Instant end = Instant.now();
            this.metrics.addSample(Duration.between(start, end).toMillis());
            ;

        }
    }

    private static class MinMaxPrinterThread extends Thread {
        private MinMaxMetrics metrics;

        public MinMaxPrinterThread(MinMaxMetrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            System.out.println("Minimum svc exec time seen till now: " + metrics.getMin() + " milliseconds");
            System.out.println("Maximum svc exec time seen till now: " + metrics.getMax() + " milliseconds");
        }
    }

    private static class MinMaxMetrics {
        // getter if called by multiple printer threads concurrently will not be atomic for the type long
        // thus, declared volatile to avoid use of synchronized in getter methods (printer thread might slow down business threads),
        private volatile long min;
        private volatile long max;

        /**
         * Initializes all member variables
         */
        public MinMaxMetrics() {
            this.min = Long.MAX_VALUE;
            this.max = Long.MIN_VALUE;
        }

        /**
         * Adds a new sample to our metrics.
         */
        public synchronized void addSample(long newSample) {
            if (newSample < min) min = newSample;
            if (newSample > max) max = newSample;
        }

        /**
         * Returns the smallest sample we've seen so far.
         */
        public long getMin() {
            return min;
        }

        /**
         * Returns the biggest sample we've seen so far.
         */
        public long getMax() {
            return max;
        }
    }
}
