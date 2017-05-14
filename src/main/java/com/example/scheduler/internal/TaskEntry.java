package com.example.scheduler.internal;

import com.example.scheduler.Task;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Entry of queue containing {@link Task}.
 */
public class TaskEntry<ResultType> implements Delayed, Comparable<Delayed> {

    public TaskEntry(final Task<ResultType> task, final long seqNumber) {
        this.task = task;
        this.seqNumber = seqNumber;
    }

    public Task<ResultType> getTask() {
        return task;
    }


    @Override
    public long getDelay(final TimeUnit unit) {
        return LocalDateTime.now().until(getTask().getScheduleTime(), timeUnit2ChronoUnit(unit));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compareTo(final Delayed other) {
        if (!(other instanceof TaskEntry))
            throw new IllegalArgumentException("supporting only TaskEntry comparison");

        return compareTo((TaskEntry<ResultType>)other);
    }


    public int compareTo(final TaskEntry<ResultType> other) {
        if (this == other)
            return 0;

        // Major comparison factor is schedule time.
        final int cmp = getTask().getScheduleTime().compareTo(other.getTask().getScheduleTime());
        if (0 != cmp)
            return cmp;

        // If scheduled to be run at the same time, first inserted task has priority.
        return Long.compare(seqNumber, other.seqNumber);
    }


    private static ChronoUnit timeUnit2ChronoUnit(final TimeUnit unit) {
        switch (unit) {
            case NANOSECONDS:
                return ChronoUnit.NANOS;
            case MICROSECONDS:
                return ChronoUnit.MICROS;
            case MILLISECONDS:
                return ChronoUnit.MILLIS;
            case SECONDS:
                return ChronoUnit.SECONDS;
            case MINUTES:
                return ChronoUnit.MINUTES;
            case HOURS:
                return ChronoUnit.HOURS;
            case DAYS:
                return ChronoUnit.DAYS;
            default:
                throw new IllegalArgumentException("Unknown TimeUnit");
        }
    }


    private final Task<ResultType> task;
    private final long seqNumber;
}
