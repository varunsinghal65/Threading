package com.varun.threading.fundamentals.coordination.joining;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * So, as you can see the main thread has a dependency on the factorial threads.
 * Unless factorial threads, finish main should not display their results.
 * We can achieve this using join operator.
 *
 * Logic :
 * Call join on each factorial thread, while in the main thread, join will not return from the factorial thread, unless
 * it's execution is competed.
 * After this step display the results in main thread.
 *
 */
public class WithJoin2 {

    public static void main(String[] args) throws InterruptedException{
        List<Long> inputNumbers = Arrays.asList(0L,3435L, 35434L, 32246L, 4656L, 23L, 5556L);

        List<FactorialThread> factorialThreads = new ArrayList<>();

        for (long inputNumber : inputNumbers) {
            factorialThreads.add(new FactorialThread(inputNumber));
        }

        for (Thread t : factorialThreads) {
            t.start();
        }

        // At this point main thread and factorial threads are running concurrently
        // join() returns from each thread only if thread execution is complete and that thread has died.
        // in summary, MAIN thread will be in WAIT state, until all factorial threads have died.
        for (Thread fThread : factorialThreads) {
          fThread.join();
        }

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
