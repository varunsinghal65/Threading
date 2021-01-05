package com.varun.threading.challenges5.deadlock4;

public class DeadlockSolution {

    public static void main(String[] args) {
        Intersection intersection = new Intersection();
        Thread trainA = new Thread(new TrainA(intersection));
        Thread trainB = new Thread(new TrainB(intersection));

        trainA.start();
        trainB.start();

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
            synchronized (roadALock) {
                System.out.println("Lock acquired on road B by :" + Thread.currentThread().getName());
                synchronized (roadBLock) {
                    System.out.println(Thread.currentThread().getName() + " : is passing through Road B");
                }
            }
        }

    }

}
