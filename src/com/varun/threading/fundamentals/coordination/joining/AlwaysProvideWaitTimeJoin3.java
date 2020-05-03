package com.varun.threading.fundamentals.coordination.joining;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * We have a problem.
 *
 * if one of the input numbers is large enough, our app will get stuck.
 * MAIN thread will always wait for thread 0 to finish.
 * So to avoid this, whenever, a join is used, ALWAYS specify the time, after which the join should return from the thread.
 *
 * We have another problem.
 *
 * Although, specifying the wait time, allowed our application to resume and not get stuck.
 * It is never terminated, because, that long computation thread is running.
 * Use setDaemon
 *
 */
public class AlwaysProvideWaitTimeJoin3 {

    public static void main(String[] args) throws InterruptedException{
        List<Long> inputNumbers = Arrays.asList(1000000000000L,3435L, 35434L, 32246L, 4656L, 23L, 5556L);

        List<FactorialThread> factorialThreads = new ArrayList<>();

        for (long inputNumber : inputNumbers) {
            factorialThreads.add(new FactorialThread(inputNumber));
        }

        for (Thread t : factorialThreads) {
            //remove this and app will not terminate because the long computation thread will still be running, after main thread dies.
            t.setDaemon(true);
            t.start();
        }

        for (Thread fThread : factorialThreads) {
            // remove this app is stuck
            fThread.join(2000);
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
