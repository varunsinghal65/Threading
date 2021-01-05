package com.varun.threading.challenges5.dataRace3;

public class SolvingDataRace {

    public static void main(String[] args) {
        SharedClass sharedObject = new SharedClass();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                sharedObject.increment();
            }
        });

        Thread t2 = new Thread(()->{
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                sharedObject.checkForDataRace();
            }
        });

        //concurrent and parallel execution starts
        t1.start();
        t2.start();
    }

    private static class SharedClass {
        /**
         * Note: Volatile marking will solve he data race problem, still interleaving of threads is happening, but since order is guaranteed
         * we can at least predict results and not have prardoxical results.
         */
        private volatile int x;
        private volatile int y;

        public SharedClass() {
            x = 0;
            y = 0;
        }

        public void increment() {
            x++;
            y++;
        }

        public void checkForDataRace() {
            if (y > x) {
                System.out.println("y > x : Data race is detected");
            }
        }
    }

}
