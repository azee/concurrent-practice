package ru.greatbit.concurrent.practice;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by azee on 23.04.15.
 */
public class HashMapThreadAwareItTest {
    private volatile boolean corrupted;
    /**
     * 1. We create a bunch of elements that will share buckets
     * 2. In each thread we add and remove the same value and verify that it was removed
     * 3. If the value is missing while removing this means that another thread placed
     * its own value in the same place of the bucket
     * 4. We iterate until a thread will find out that its value is missing
     */
    public void hashMapIsNotThreadSafeTest(final Map<Integer, Object> map, boolean expectCorruption)
            throws InterruptedException {
        corrupted = false;
        final int THREADS = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        for (int i = 0; i < THREADS; i++){
            final int threadId = i;
            Runnable worker = new Runnable() {
                @Override
                public void run() {
                    while(!corrupted){
                        map.put(threadId, new Object());
                        corrupted = !map.containsKey(threadId);
                        map.remove(threadId);
                    }
                }
            };
            executor.execute(worker);
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(expectCorruption, corrupted);
    }

    @Test
    public void HashMapUnsafeTest() throws InterruptedException {
        hashMapIsNotThreadSafeTest(new HashMap<Integer, Object>(), true);
        hashMapIsNotThreadSafeTest(new ConcurrentHashMap<Integer, Object>(), false);
    }

}
