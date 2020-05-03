package com.varun.threading.fundamentals.creation;

public class ThreadInheritance4 {
    public static void main(String[] args) {
        Thread t1 = new NewThread();
        t1.start();
    }

    /**
     * In our previous approach, to create and schedule a thread, we need to create an object of Thread and also
     * create an object of Runnable. We can merge these 2 actions into 1, by extending Thread, which in turn
     * implements Runnable.
     */
    private static class NewThread extends Thread {
        @Override
        public void run() {
            System.out.println("Hello from" + Thread.currentThread().getName());
            // we can access the thread utilities directly, without retrieving the current thread object
            System.out.println("Hello from" + this.getName());

        }
    }
}
