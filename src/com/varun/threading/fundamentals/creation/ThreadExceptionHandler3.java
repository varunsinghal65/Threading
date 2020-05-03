package com.varun.threading.fundamentals.creation;

public class ThreadExceptionHandler3 {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                throw new RuntimeException("Intentional exception");
            }
        });

        t1.setName("Misbehaving Thread");
        t1.setPriority(Thread.MAX_PRIORITY);
        /**
         * If unchecked exception (exception not visible during CT) is thrown by the thread,
         * it will simply bring down the entire thread, until we catch them and handle them in a particular way
         */
        t1.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("A critical error happened in thread:" + t.getName() + ", with the following exception:  " + e.getMessage());
            }
        });

        System.out.println("We are in thread: " + Thread.currentThread().getName() + " before starting a new thread");
        t1.start();
        System.out.println("We are in thread: " + Thread.currentThread().getName() + " after starting a new thread");
    }
}
