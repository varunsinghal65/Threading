package com.varun.threading.experiments;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Experiment {

    public static void main(String[] args) throws InterruptedException {
        List<Long> inputNumbers = Arrays.asList(100000000L, 0L, 3435L, 35434L, 3224L, 4656L, 5L, 5556L);

        List<FactorialComputationThread> fThreads = new ArrayList<>();

        for (long input : inputNumbers) {
            fThreads.add(new FactorialComputationThread(input));
        }

        for (FactorialComputationThread thread : fThreads) {
            thread.setDaemon(true);
            thread.start();
        }

        for (FactorialComputationThread thread : fThreads) {
            thread.join(2000);
        }

        for (FactorialComputationThread thread : fThreads) {
            if (thread.isFinished()) {
                System.out.println("Factorial of " + thread.getInputNumber() + ": " + thread.getResult());
            } else {
                System.out.println("Factorial of " + thread.getInputNumber() + " is still in progress.");
            }
        }
    }

    private static class FactorialComputationThread extends Thread {
        private BigInteger result;
        private boolean isFinished;
        private long inputNumber;

        public FactorialComputationThread(long inputNumber) {
            this.inputNumber = inputNumber;
            isFinished = false;
            result = BigInteger.ONE;
        }

        @Override
        public void run() {
            for (long i = inputNumber; i > 0; i--) {
                result = result.multiply(new BigInteger(Long.toString(i)));
            }
            isFinished = true;
        }

        public long getInputNumber() {
            return inputNumber;
        }

        public BigInteger getResult() {
            return result;
        }

        public boolean isFinished() {
            return isFinished;
        }

    }
}
