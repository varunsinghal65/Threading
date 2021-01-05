package com.varun.threading.challenges5.VolatileExample2;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

/**
 * AIM :
 * <p>
 * To show how we can perform effective collection of service time samples and print them in parallel
 * using volatile keyword.
 *
 * VOLATILE keyword makes reading and writing to long and double thread safe or atomic operations.
 */
public class ServiceTimeMetricExample {

    public static void main (String[] args) {
        Metrics metrics = new Metrics();
        BusinessThread bt1 = new BusinessThread(metrics);
        BusinessThread bt2 = new BusinessThread(metrics);
        MetricPrinterThread avgExecTimePrinter = new MetricPrinterThread(metrics);

        bt1.start();
        bt2.start();
        avgExecTimePrinter.start();
    }

    private static class BusinessThread extends Thread {

        private Metrics metrics = null;

        public BusinessThread(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while (true) {
                Random randomSleepTime = new Random();
                Instant start = Instant.now();
                try {
                    Thread.sleep(randomSleepTime.nextInt(10));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Instant end = Instant.now();
                this.metrics.addSample(Duration.between(start, end).toMillis());
            }
        }

        @Override
        public void start() {
            this.setName("Business-thread-X");
            super.start();
        }
    }

    private static class MetricPrinterThread extends Thread {
        private Metrics metrics = null;

        public MetricPrinterThread(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Average Service Exec time: " + this.metrics.getAverage());
            }
        }

        @Override
        public synchronized void start() {
            this.setName("Avg-printer-thread");
            super.start();
        }
    }

    private static class Metrics {

        private volatile long count = 0L;
        /**
         * Remember double is a 64 bit binary as compared to int, a 32 bit binary
         * Also, we cannot use long, average is expected to have decimals.
         * Using long will screw our calculation.
         */
        private volatile double average = 0.0;

        /**
         * Method will be accessed concurrently by business threads, need to be synchronized, its not atomic operation.
         *
         * @param sample
         */
        public synchronized void addSample(long sample) {
            double sampleSumTillNow = average * count;
            count++;
            this.average = (sampleSumTillNow + sample) / count;
        }

        /**
         * Since many threads might access this method concurrently (multitasking)
         * and assignment of a long is not an atomic operation
         * while average is being assigned to register for return
         * assignment happens in 2 ops (First, copy upper 32bit, second copy lower 32 bits)
         * Some other thread might change the lower 32 bit during copy.
         * So we have to synchronise it.
         * <p>
         * Now, adding a synchronized keyword will introduce a performance loss. Why ??
         * <p>
         * If thread X calls getAverage() first all business threads will be denied access to addSample() method
         * causing them to wait and vice versa.
         * <p>
         * So solution is we introduce volatile keyword while declaring long and double --> this tells JVM to ensure that this
         * assignment becomes a atomic operation.
         */
        public double getAverage() {
            return this.average;
        }

        public long getCount() {
            return this.count;
        }

    }

}
