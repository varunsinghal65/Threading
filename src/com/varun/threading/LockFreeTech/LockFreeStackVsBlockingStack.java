package com.varun.threading.LockFreeTech;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeStackVsBlockingStack {

    public static void main(String[] args) throws InterruptedException {
        benchMarkStack();
    }

    private static void benchMarkStack() throws InterruptedException {
        //StandardBlockingThreadSafeStack<Integer> stack = new StandardBlockingThreadSafeStack<>();
        LockFreeThreadsafeStack<Integer> stack = new LockFreeThreadsafeStack<>();
        Random random = new Random();
        //create pushing and popping threads
        int pushingThreads = 1;
        int poppingThreads = 1;
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < pushingThreads; i++) {
            Thread t = new Thread(() -> {
                while (true) {
                    stack.push(random.nextInt());
                }
            });
            t.setDaemon(true);
            threads.add(t);
        }
        for (int i = 0; i < poppingThreads; i++) {
            Thread t = new Thread(() -> {
                while(true) {
                    stack.pop();
                }
            });
            t.setDaemon(true);
            threads.add(t);
        }
        //tell OS to schedule them
        for (Thread t : threads) {
            t.start();
        }
        //Put main thread to sleep, allowing other threads time to interact with stack
        Thread.sleep(5000);

        //Check the operations performed on the stack by the threads
        System.out.println(String.format("The number of operations performed on the stack in 5 seconds: %,d", stack.getCounter()/1000000));
    }

    private static void testThreadSafeStack() throws InterruptedException {
        //StandardBlockingThreadSafeStack<Integer> intStandardStack = new StandardBlockingThreadSafeStack<>();
        LockFreeThreadsafeStack<Integer> stack = new LockFreeThreadsafeStack<>();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                stack.push(i);
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                stack.pop();
            }
        });
        t1.start();
        t1.join();
        t2.start();
        t2.join();
        System.out.print("Stack : ");
        stack.printStack();

        System.out.println("Number of operations executed: " + stack.getCounter());
    }

    private static class LockFreeThreadsafeStack<T> {
        private AtomicReference<StackNode<T>> atomicRefHead = new AtomicReference<>();
        private AtomicInteger atomicCounter = new AtomicInteger(0);

        public void push(T value) {
            StackNode<T> nodeToBePushed = new StackNode<>(value);
            while (true) {
                // read op
                StackNode<T> currentHeadRef = atomicRefHead.get();
                nodeToBePushed.next = currentHeadRef;
                //write op, if the current head is still the same as the head, that was read.
                if (atomicRefHead.compareAndSet(currentHeadRef, nodeToBePushed)) {
                    break;
                }
            }
            atomicCounter.incrementAndGet();
        }

        public void pop() {
            StackNode<T> currentHeadRef = atomicRefHead.get();
            while (currentHeadRef != null) {
                //read op
                StackNode<T> newHead = currentHeadRef.next;
                // write op
                if (atomicRefHead.compareAndSet(currentHeadRef, newHead)) {
                    break;
                } else {
                    currentHeadRef = atomicRefHead.get();
                }
            }
            atomicCounter.incrementAndGet();
        }

        public void printStack() {
            StackNode<T> pointer = null;
            synchronized (this) {
                pointer = atomicRefHead.get();
            }
            while (pointer != null) {
                if (pointer.next == null) {
                    System.out.println(pointer.getNodeValue());
                } else {
                    System.out.print(pointer.getNodeValue() + ",");
                }
                pointer = pointer.next;
            }
        }

        public int getCounter() {
            return atomicCounter.get();
        }
    }

    private static class StandardBlockingThreadSafeStack<T> {
        private StackNode<T> head;
        private int counter;

        public StandardBlockingThreadSafeStack() {
            head = null;
            counter = 0;
        }

        /**
         * push and pop operations when executed concurrently can develop race condition
         * between reading and writing head. To avoid them, we can use locks, that suspend/block a thread
         * if the lock cannot be acquired by it.
         */
        public void push(T valueToPush) {
            StackNode<T> nodeToPush = new StackNode<T>(valueToPush);
            synchronized (this) {
                //read reference in head
                nodeToPush.next = head;
                //write head OR change reference in head
                head = nodeToPush;
                counter++;
            }
        }

        public void pop() {
            synchronized (this) {
                if (head != null) {
                    // read head
                    StackNode<T> newHead = head.next;
                    // write head
                    head = newHead;
                    counter++;
                }
            }
        }

        public int getCounter() {
            return counter;
        }

        public void printStack() {
            StackNode<T> pointer = null;
            synchronized (this) {
                pointer = head;
            }
            while (pointer != null) {
                if (pointer.next == null) {
                    System.out.println(pointer.getNodeValue());
                } else {
                    System.out.print(pointer.getNodeValue() + ",");
                }
                pointer = pointer.next;
            }
        }
    }

    private static class StackNode<T> {
        private T value;
        private StackNode<T> next;

        public T getNodeValue() {
            return value;
        }

        public StackNode(T value) {
            this.value = value;
            this.next = null;
        }
    }
}
