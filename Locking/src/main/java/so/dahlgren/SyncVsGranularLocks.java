package so.dahlgren;

import org.apache.commons.cli.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.ArrayList;
import java.util.List;
/**
* SyncVsGranularLocks - Verify the performance characteristics of synchronizing access
* to an object, versus using granular read-write locks.
* @author rdahlgren
* @created 19Apr2013 13:46:47
*/
public class SyncVsGranularLocks {
	private static final int dataSize = 1024 * 10;
	private static final int mask = dataSize - 1; // See previous mod vs AND test
	private static final long[] dataBlock = new long[dataSize];

	private static final Object syncObject = new Object();
	private static final ReentrantReadWriteLock locks = new ReentrantReadWriteLock(true);

	// For great convenience!
	private static class AppData {
		public boolean useLocks = false;
		public int readerThreads = 0;
		public int writerThreads = 0;
		public long millisToRun = 0l;
	}

	private static class ReaderLogic implements Runnable {
		private static final AtomicInteger offset = new AtomicInteger(0);
		private final long millisToRun;
		private final CountDownLatch start;
		private final CountDownLatch stop;
		private final boolean useLocks;

		public long readsPerformed = 0l;

		public ReaderLogic(
			long millisToRun, CountDownLatch start, CountDownLatch stop, boolean useLocks
		) { 
			this.millisToRun = millisToRun; 
			this.start = start;
			this.stop = stop;
			this.useLocks = useLocks;
		}

		public void run() {
			// So every reader isn't trying to read the same index, just to make the test more interesting
			int counter = offset.getAndIncrement() * 4;
			try { start.await(); }
			catch (InterruptedException ex) { // Die
				return;
			}
			long startTime = System.currentTimeMillis();
			long stopTime = startTime + millisToRun;
			while (System.currentTimeMillis() < stopTime) {
				if (useLocks) lockLogic(counter);
				else syncLogic(counter);
				readsPerformed++;
			}
			stop.countDown();
		}

		private long syncLogic(int counter) {
			synchronized(syncObject) {
				long dataVal = SyncVsGranularLocks.dataBlock[
					counter++ & SyncVsGranularLocks.mask
				];
				return dataVal;
			}
		}

		private long lockLogic(int counter) {
			locks.readLock().lock();
			try {
				long dataVal = SyncVsGranularLocks.dataBlock[
					counter++ & SyncVsGranularLocks.mask
				];
				return dataVal;
			} finally {
				locks.readLock().unlock();
			}
		}
	}

	private static class WriterLogic implements Runnable {
		private static final AtomicInteger offset = new AtomicInteger(2);
		private final long millisToRun;
		private final CountDownLatch start;
		private final CountDownLatch stop;
		private final boolean useLocks;

		public long writesPerformed = 0l;

		public WriterLogic(
			long millisToRun, CountDownLatch start, CountDownLatch stop, boolean useLocks
		) { 
			this.millisToRun = millisToRun; 
			this.start = start;
			this.stop = stop;
			this.useLocks = useLocks;
		}

		public void run() {
			int counter = offset.getAndIncrement() * 4;
			try { start.await(); }
			catch (InterruptedException ex) { // Just die :-/
				return;
			}
			long startTime = System.currentTimeMillis();
			long stopTime = startTime + millisToRun;
			while (System.currentTimeMillis() < stopTime) {
				if (useLocks) lockLogic(counter);
				else syncLogic(counter);
				writesPerformed++;
			}
			stop.countDown();
		}

		private void syncLogic(int counter) {
			synchronized(syncObject) {	
				SyncVsGranularLocks.dataBlock[counter++ & SyncVsGranularLocks.mask] = counter;
			}
		}

		private void lockLogic(int counter) {
			locks.writeLock().lock();
			try {
				SyncVsGranularLocks.dataBlock[counter++ & SyncVsGranularLocks.mask] = counter;
			} finally {
				locks.writeLock().unlock();
			}
		}
	}

	/**
	 * Kick off the test. 
	 */
	public static void main(String[] args) throws InterruptedException {
		AppData data = parseOptions(args);
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch stop = new CountDownLatch(data.readerThreads + data.writerThreads);
		List<ReaderLogic> readers = new ArrayList<ReaderLogic>(data.readerThreads);
		List<WriterLogic> writers = new ArrayList<WriterLogic>(data.writerThreads);
		// Readers
		for (int i = 0; i < data.readerThreads; i++) {
			ReaderLogic reader = new ReaderLogic(data.millisToRun, start, stop, data.useLocks);
			readers.add(reader);
			new Thread(reader).start();
		}
		// Writers
		for (int i = 0; i < data.writerThreads; i++) {
			WriterLogic writer = new WriterLogic(data.millisToRun, start, stop, data.useLocks);
			writers.add(writer);
			new Thread(writer).start();
		}

		System.out.println("Beginning " + (data.millisToRun / 1000.0) + " second run.");
		start.countDown();
		stop.await();
		System.out.println("Run complete.");
		for (ReaderLogic reader : readers) {
			System.out.println("Reader performed " + reader.readsPerformed + " read ops.");
		}
		for (WriterLogic writer : writers) {
			System.out.println("Writer performed " + writer.writesPerformed + " write ops.");
		}
	}

	private static AppData parseOptions(String[] args) {
		Option accessMethod = OptionBuilder.withArgName("method")
														.hasArg()
														.withDescription("Specify sync | locks")
														.isRequired()
														.create("accessmethod");
		Option readerThreads = OptionBuilder.withArgName("number")
														.hasArg()
														.withDescription("Number of reader threads")
														.isRequired()
														.create("readerthreads");
		Option writerThreads = OptionBuilder.withArgName("number")
														.hasArg()
														.withDescription("Number of writer threads")
														.isRequired()
														.create("writerthreads");
		Option testDuration = OptionBuilder.withArgName("duration")
														.hasArg()
														.withDescription("Test duration, specified in seconds")
														.isRequired()
														.create("duration");

		Options options = new Options();
		options.addOption(accessMethod);
		options.addOption(readerThreads);
		options.addOption(writerThreads);
		options.addOption(testDuration);

		CommandLineParser parser = new BasicParser();
		AppData retVal = new AppData();
		try {
			CommandLine line = parser.parse(options, args);
			retVal.useLocks = line.getOptionValue("accessmethod").equals("locks");
			retVal.readerThreads = Integer.parseInt(line.getOptionValue("readerthreads"));
			retVal.writerThreads = Integer.parseInt(line.getOptionValue("writerthreads"));
			retVal.millisToRun = Long.parseLong(line.getOptionValue("duration")) * 1000l;
		} catch (MissingOptionException ex) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("App", options);
			System.exit(-1);
		} catch (ParseException ex) {
			// Can't really recover from this anyway
			throw new IllegalStateException("Problem parsing arguments", ex);
		} catch (NumberFormatException ex) {
			// Same as above
			throw new IllegalArgumentException("Number format was incorrect", ex);
		}
		return retVal;
	}
/* vim: set ts=2 sw=2 et: */
} /// end of SyncVsGranularLocks
