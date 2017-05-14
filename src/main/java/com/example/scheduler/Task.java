package com.example.scheduler;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

/**
 * Task to be processed.
 */
public class Task<ResultType> implements Callable<ResultType> {

    /**
     * Constructs a task.
     *
     * @param runAt scheduled run time
     * @param callable task essence
     */
    public Task(final LocalDateTime runAt, final Callable<ResultType> callable) {
        this.runAt = runAt;
        this.callable = callable;
    }


    /**
     * Get scheduled run time.
     */
    public LocalDateTime getScheduleTime() {
        return runAt;
    }

    /**
     * Run the task.
     */
    @Override
    public ResultType call() throws Exception {
        return callable.call();
    }


    private final LocalDateTime runAt;
    private final Callable<ResultType> callable;
}
