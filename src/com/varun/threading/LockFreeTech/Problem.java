package com.varun.threading.LockFreeTech;

import com.sun.jndi.toolkit.ctx.AtomicContext;

import java.util.concurrent.atomic.AtomicInteger;

public class Problem {

    public static void main(String[] args) throws InterruptedException {
        AtomicIntWrapper aIntWrapper = new AtomicIntWrapper(new AtomicInteger(0));
        Thread t1 = new Thread(()->{
            aIntWrapper.customAdd();
        });
        Thread t2 = new Thread(()->{
            aIntWrapper.customAdd();
        });
        Thread t3 = new Thread(()->{
            aIntWrapper.customAdd();
        });

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println(aIntWrapper.getFinalValue().toString());
    }

    private static class AtomicIntWrapper {
        private AtomicInteger aInt = null;
        public AtomicIntWrapper(AtomicInteger aInt) {
            this.aInt = aInt;
        }
        public void customAdd() {
            aInt.incrementAndGet();
            aInt.addAndGet(-10);
        }
        public AtomicInteger getFinalValue() {
            return aInt;
        }
    }
}
