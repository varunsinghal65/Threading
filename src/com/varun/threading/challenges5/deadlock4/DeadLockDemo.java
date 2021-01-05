package com.varun.threading.challenges5.deadlock4;

/**
 * Aim : to write a program for a traffic control system that will try to avoid collisions between trains running on 2 lines perpendicular to each other.
 * In the process, show deadlock
 */
public class DeadLockDemo {

    public static void main(String[] args) {
        Intersection intersection = new Intersection();
        Thread trainA = new Thread(new TrainA(intersection));
        Thread trainB = new Thread(new TrainB(intersection));

        /**
         * Deadlock will happen when trainA thread and trainB thread run in parallel OR run concurrently.
         *
         * Sample concurrent or parallel exec :
         *
         * trainA thread scheduled, acquired lock on A.
         * trainA out
         * trainB thread scheduled, acquired lock on B.
         * trainB out
         * train A scheduled, cannot acquire lock on B.
         * train B scheduled, cannot acquire lock on A.
         *
         * Deadlock achieved.
         */
        trainA.start();
        trainB.start();

        Thread monitoringThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    System.out.println("Thread state: " + trainA.getState());

                }
            }
        });
        monitoringThread.start();

    }

    private static class TrainA implements Runnable {
        private Intersection intersection;
        public TrainA(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            intersection.takeRoadA();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class TrainB implements Runnable {
        private Intersection intersection;
        public TrainB(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            intersection.takeRoadB();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Intersection {
        Object roadALock = new Object();
        Object roadBLock = new Object();

        public void takeRoadA() {
            synchronized (roadALock) {
                System.out.println("Lock acquired on road A by :" + Thread.currentThread().getName());
                synchronized (roadBLock) {
                    System.out.println(Thread.currentThread().getName() + " : is passing through Road A");
                }
            }
        }

        public void takeRoadB() {
            synchronized (roadBLock) {
                System.out.println("Lock acquired on road B by :" + Thread.currentThread().getName());
                synchronized (roadALock) {
                    System.out.println(Thread.currentThread().getName() + " : is passing through Road B");
                }
            }
        }

    }

}
