package com.varun.threading.fundamentals1.creation1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Story summary :
 *
 * Vault is protected by a int password.
 * Hackers have 10 seconds to break in the vault, after 10 seconds are over, police will catch them.
 *
 * Logic summary :
 *
 * AscendingThread - a hacker thread, will try to guess password by iterating till MAX_PASSWORD starting from 1, if guessed correctly, exit program
 * DescendingThread - a hacker thread, will try to guess password by iterating till 0 starting from MAX_PASSWORD, if guessed correctly, exit program
 * PoliceThread - a police thread, will count till 10 seconds, post that, program exit
 * HackerThread - A abstract class for any concrete hacker thread
 *
 * <b>Note</b>:
 * All the above thread classes extend thread, which implements Runnable, each of them is a fully functional thread.
 *
 */
public class ThreadingCaseStudy5 {
    public static final int MAX_PASSWORD = 9999;

    public static void main(String[] args) {
        Random random = new Random();
        Vault vault = new Vault(random.nextInt(MAX_PASSWORD));

        List<Thread> threads = new ArrayList<>();
        threads.add(new AscendingThread(vault));
        threads.add(new DescendingThread(vault));
        threads.add(new PoliceThread());

        for (Thread thread: threads) {
            thread.start();
        }
    }

    private static class Vault {
        int password;

        public Vault(int password) {
            System.out.println("Vault password initialized to: " + password);
            this.password = password;
        }

        public boolean isPasswordCorrect(int pass) {
            try {
                //delay the potential hacker thread
                Thread.sleep(5);
            } catch (InterruptedException e) {
            }
            return pass == password;
        }
    }

    private static abstract class HackerThread extends Thread {
        protected Vault vault;

        public HackerThread(Vault vault) {
            this.vault = vault;
            this.setName(this.getClass().getName());
            this.setPriority(Thread.MAX_PRIORITY);
        }

        public void start() {
            System.out.println("Starting thread" + this.getName());
            super.start();
        }
    }

    private static class AscendingThread extends HackerThread {
        public AscendingThread(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int guess = 0; guess < MAX_PASSWORD; guess++) {
                if (vault.isPasswordCorrect(guess)) {
                    System.out.println("Thread : " + this.getName() + ", guessed the password " + guess);
                    System.exit(0);
                }
            }
        }
    }

    private static class DescendingThread extends HackerThread {
        public DescendingThread(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int guess = MAX_PASSWORD; guess >= 0; guess--) {
                if (vault.isPasswordCorrect(guess)) {
                    System.out.println("Thread : " + this.getName() + ", guessed the password " + guess);
                    System.exit(0);
                }
            }
        }
    }

    private static class PoliceThread extends Thread {
        @Override
        public void run() {
            //mimicking a 10 second timer
            for (int i = 10; i >= 0; i--) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
                System.out.println(i);
            }
            System.out.println("Game over for you hackers");
            System.exit(0);
        }
    }
}
