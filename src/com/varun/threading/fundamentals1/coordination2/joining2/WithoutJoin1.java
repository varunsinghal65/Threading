package com.varun.threading.fundamentals1.coordination2.joining2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Factorial op is CPU intensive, so to speed up performance, we will create a new thread,
 * for each number whose factorial is to be calculated.
 *
 * Output explanation:
 * The factorial threads and main thread(line:34) are in RACE.
 * By the time main thread checks for finished status, factorial threads with small inputs would have finished and
 * would display the results.
 *
 * */
public class WithoutJoin1 {

    public static void main(String[] args) {
        List<Long> inputNumbers = Arrays.asList(0L,3435L, 35434L, 3224L, 4656L, 23L, 5556L);

        List<FactorialThread> factorialThreads = new ArrayList<>();

        for (long inputNumber : inputNumbers) {
            factorialThreads.add(new FactorialThread(inputNumber));
        }

        for (Thread t : factorialThreads) {
            t.start();
        }

        // At this point main thread and factorial threads are running concurrently
        for (FactorialThread fThread : factorialThreads) {
            if (fThread.isFinished()) {
                System.out.println("Factorial of input number:" + fThread.getInputNumber() +", is" + fThread.getResult());
            } else {
                System.out.println("Factorial of input number:" + fThread.getInputNumber() +", is still in progress");
            }
        }
    }

    private static class FactorialThread extends Thread {
        private BigInteger result = BigInteger.ONE;
        private boolean isFinished = false;
        private long inputNumber = 0L;

        public FactorialThread(long inputNumber) {
            this.inputNumber =  inputNumber;
        }

        public void run() {
            this.result = this.calculateFactorial(this.inputNumber);
            this.isFinished = true;
        }

        public BigInteger calculateFactorial(long inputNumber) {
            BigInteger result =  BigInteger.ONE;
            for (long i = inputNumber; i>0;i--) {
                result = result.multiply(new BigInteger(Long.toString(i)));
            }
            return result;
        }

        public BigInteger getResult() {
            return result;
        }

        public boolean isFinished() {
            return isFinished;
        }

        public long getInputNumber() {
            return inputNumber;
        }
    }
}
