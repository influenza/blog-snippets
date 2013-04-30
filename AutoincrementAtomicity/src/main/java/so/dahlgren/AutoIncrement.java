// Copyright 2012, Buffalo Studios. All rights reserved.
package so.dahlgren;

import java.util.concurrent.CountDownLatch;

/**
* AutoIncrement - Demonstrate that integer autoincrement isn't atomic.
* @author rdahlgren
* @created 29Apr2013 17:00:46
*/
public class AutoIncrement {
	private static int x = 0;

	// Indicates reality has broken
	public static class ItBroke extends RuntimeException { }

	// Performs the autoincrement
	public static class Mutator implements Runnable {
		private final int iterations;
		private final CountDownLatch start;
		private final CountDownLatch stop;

		/**
		 * Construct a powerful and fearsome mutator.
		 * @param iterations Number of times the autoincrement should be performed
		 * @param start CountDownLatch used to signal the test should start
		 * @param stop CountDownLatch used to signal the test should stop
		 */
		public Mutator(int iterations, CountDownLatch start, CountDownLatch stop) { 
			this.iterations = iterations; this.start = start; this.stop = stop;
		}
		// Do the thing
		public void run() {
			try { start.await(); } catch (InterruptedException ex) { 
				throw new RuntimeException("Interrupted."); 
			}
			for (int i = 0; i < iterations; ++i) { ++x; }
			stop.countDown(); // Signal completion
		}
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: java -jar ThisJarFile.jar threads iterations");
			System.out.println("Example: java -jar ThisJarFile.jar 2 10");
			System.exit(-1);
		}

		int numThreads = Integer.parseInt(args[0]);
		int iterations = Integer.parseInt(args[1]); // Lots of operations

		System.out.println("Starting test.");
		long startTime = System.currentTimeMillis();
		long stopTime = 0l;
		long successes = 0l;
		try {
			while (true) { doTheTest(numThreads, iterations); successes++; }
		} catch (ItBroke ex) {
			stopTime = System.currentTimeMillis();
		}
		float duration = (stopTime - startTime) / 1000.0f;
		System.out.println("Broke after " + duration + " seconds.");
		System.out.println("Successful runs: " + successes);
	}

	/**
	 * Actual logic for the test.
	 * @param numThreads number of mutators that should be summoned
	 * @param iterations number of times to perform the autoincrement
	 */
	private static void doTheTest(int numThreads, int iterations) {
		AutoIncrement.x = 0; // Reset the static state
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch stop = new CountDownLatch(numThreads);
		for (int i = 0; i < numThreads; ++i) {
			new Thread(new Mutator(iterations, start, stop)).start();
		}
		start.countDown();
		try { stop.await(); } catch (Exception ex) { throw new RuntimeException("Interrupted"); }
		if (x != iterations * numThreads) { 
			System.out.println(
				"x (" + x + ") != iterations * numThreads (" + (iterations * numThreads) + ")"
			);
			throw new ItBroke(); 
		}
	}
/* vim: set ts=2 sw=2 et: */
} /// end of AutoIncrement
