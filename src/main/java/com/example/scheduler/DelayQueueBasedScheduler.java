package com.example.scheduler;

import com.example.scheduler.internal.TaskEntry;

import java.util.concurrent.DelayQueue;

/**
 * Thread-safe implementation of IScheduler.
 *
 * @param <ResultType> type of result returned by Callable.
 */
public class DelayQueueBasedScheduler<ResultType> implements IScheduler<ResultType> {

    @Override
    public void addTask(final Task<ResultType> task) {
        synchronized (lock) {
            // All further actions are non-blocking

            checkIfOperable();

            if (null == processor) {
                processor = new ProcessorThread();
                processor.start();
            }

            final TaskEntry<ResultType> newTaskEntry = new TaskEntry<>(task, insertionSequence++);
            backlog.put(newTaskEntry);

            // Checking if we should adjust current sleep call
            if (backlog.peek() == newTaskEntry)
                processor.interrupt();
        }
    }

    @Override
    public void stop(final boolean runRemaining) throws InterruptedException {
        synchronized (lock) {
            checkIfOperable();

            // stop() must be synchronized: only one concurrent thread must accomplish stop without exception
            // and this thread only is responsible for joining processor.
            stopped = true;

            if (! runRemaining)
                backlog.clear();

            if (null != processor) {
                processor.interrupt();  // interrupt take() in case of empty queue
                processor.join();
            }
        }
    }


    private void checkIfOperable() {
        if (stopped)
            throw new IllegalStateException("Scheduler is stopped");
    }


    private final Object lock = new Object();

    private final DelayQueue<TaskEntry<ResultType>> backlog = new DelayQueue<>();
    private long insertionSequence = 0L;

    private Thread processor = null;
    private volatile boolean stopped = false;


    /**
     * Thread which runs tasks according to schedule.
     */
    private class ProcessorThread extends Thread {
        @Override
        public void run() {
            TaskEntry<ResultType> entry;
            for (;;) {
                if (stopped && null == backlog.peek())
                    break;

                try {
                    entry = backlog.take();
                } catch (InterruptedException exc) {
                    // Possibly backlog was updated, we should recheck.
                    continue;
                }

                try {
                    entry.getTask().call();
                }
                catch (Exception exc) {
                    // Escalating
                    throw new RuntimeException("Fatal condition", exc);
                }
            }
        }
    }
}
