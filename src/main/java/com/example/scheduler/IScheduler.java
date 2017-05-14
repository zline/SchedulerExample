package com.example.scheduler;

/**
 * Delayed task processing scheduler.
 *
 * @param <ResultType> type of result returned by Callable.
 */
interface IScheduler<ResultType> {

    /**
     * Adds task to be run.
     */
    void addTask(Task<ResultType> task);

    /**
     * Stops the scheduler.
     *
     * <p>After call to this method any further calls are prohibited.</p>
     *
     * <p>Blocks current thread until the scheduler is stopped.</p>
     *
     * @param runRemaining if set to true, remaining backlog will be processed according to schedule,
     *                     otherwise - not processed at all.
     */
    void stop(boolean runRemaining) throws InterruptedException;
}
