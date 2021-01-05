package com.varun.threading.fundamentals1.creation1;

public class ThreadCreation1 {

	/**
	 * Output explanation :
		We are in thread: main before starting a new thread
		We are in thread: main after starting a new thread
		We are in a new thread: Thread-0
		
		1. t1.start() ==> will not immediately execute the thread.
		2. OS takes time to schedule it on CPU and this happens asynchronously.
		3. Thus, line 34 still prints "main" as the thread name.
	 */
	
	public static void main(String[] args) throws InterruptedException {
		// All thread related properties and methods are encapsulated in Thread class by
		// the JDK
		// To create a new thread, we need to instantiate this class.
		// and pass a instance of a class implementing the Runnable interface.
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				// code that will run as soon as this thread is scheduled by the OS
				System.out.println("We are in a new thread: " + Thread.currentThread().getName());
			}
		});

		System.out.println("We are in thread: " + Thread.currentThread().getName() + " before starting a new thread");
		
		// will instruct JVM to create a new thread and pass it to the OS
		t1.start();
		
		System.out.println("We are in thread: " + Thread.currentThread().getName() + " after starting a new thread");
		
		// Instructs OS not to schedule the current thread, until the specified time lapses (Also 0 CPU consumption during that time)
		Thread.sleep(10000);
	}
	
}
