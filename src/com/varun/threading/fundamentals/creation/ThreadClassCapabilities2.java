package com.varun.threading.fundamentals.creation;

/**
 * USE only Intellij idea for debugging, eclipse will suspend the current thread on BP and will also allow other threads to progress.
 *
 * Intellij on the other hand will pasue the execution of all threads and give you a nice snapshot of the other threads.
 */
public class ThreadClassCapabilities2 {

	public static void main(String[] args) throws InterruptedException {
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				//BP3: Main AND New Worker Thread active
				System.out.println("We are in a new thread: " + Thread.currentThread().getName());
				//BP4: Main thread gone, only New Worker Thread active
				System.out.println("Current thread prio: " + Thread.currentThread().getPriority());
			}
		});

		t1.setName("New Worker Thread");
		// sets the static priority variable, in the dynamic priority of the thread
		t1.setPriority(Thread.MAX_PRIORITY);

		//BP1 : Main thread will be active in debug thread window
		System.out.println("We are in thread: " + Thread.currentThread().getName() + " before starting a new thread");
		t1.start();
		//BP2 : Main thread will still be active in debug window, "new worker thread" still not created, as it takes time to schedule by the OS
		System.out.println("We are in thread: " + Thread.currentThread().getName() + " after starting a new thread");

		// BPX: check the debug trace for fun, entire sequence of BP will change
		//Thread.sleep(10000);
	}
}
