package com.example.scheduler;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class TestDelayQueueBasedScheduler extends Assert {

    @Test
    public void testEmptyCase() throws InterruptedException {
        new DelayQueueBasedScheduler<Void>().stop(false);
        new DelayQueueBasedScheduler<Void>().stop(true);
    }

    @Test
    public void testStoppingEmptyScheduler() throws InterruptedException {
        final IScheduler<Void> scheduler = new DelayQueueBasedScheduler<Void>();
        scheduler.addTask(new Task<>(mkTime(-1), EMPTY_CALLABLE));
        Thread.sleep(1000);
        // This call must not hang forever
        scheduler.stop(false);
    }
    @Test
    public void testPerformingOneTask() throws InterruptedException {
        final int[] tasksPerformed = new int[]{0};
        final Callable<Void> task = () -> {tasksPerformed[0]++; return null;};

        for (final long secondsFromNow : new long[]{-1, 0, 1}) {
            final IScheduler<Void> scheduler = new DelayQueueBasedScheduler<Void>();
            scheduler.addTask(new Task<>(mkTime(secondsFromNow), task));
            scheduler.stop(true);
        }
        assertEquals(3, tasksPerformed[0]);
    }

    @Test
    public void testOrderOfExecution() throws InterruptedException {
        final List<String> result = new ArrayList<>();
        final IScheduler<Void> scheduler = new DelayQueueBasedScheduler<Void>();

        scheduler.addTask(new Task<>(mkTime(1), () -> {result.add("bar"); return null;}));
        scheduler.addTask(new Task<>(mkTime(2), () -> {result.add("baz"); return null;}));
        scheduler.addTask(new Task<>(mkTime(2), () -> {result.add("baz2"); return null;}));
        scheduler.addTask(new Task<>(mkTime(-1), () -> {result.add("foo"); return null;}));

        scheduler.stop(true);
        assertEquals(Arrays.asList("foo", "bar", "baz", "baz2"), result);
    }


    private static LocalDateTime mkTime(long secondsFromNow) {
        return LocalDateTime.now().plus(secondsFromNow, ChronoUnit.SECONDS);
    }

    private static final Callable<Void> EMPTY_CALLABLE = () -> null;
}
