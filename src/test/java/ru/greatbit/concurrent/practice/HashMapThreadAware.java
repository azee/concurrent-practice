package ru.greatbit.concurrent.practice;

import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by azee on 23.04.15.
 */
public class HashMapThreadAware {

    /**
     * Even though we call add after removal we will gen an empty map
     * @throws InterruptedException
     */
    @Test
    public void hashMapIsNotThreadSafeTest() throws InterruptedException {
        HashMap<Object, String> map = new HashMap<Object, String>();
        hashMapThreadAwareTest(map);
        assertThat(map.size(), is(0));
    }

    /**
     * It is a test on a test.
     * This test won't fail because put is synced using compare-and-swap
     * Removal and second put won't be called while equals() is executing in
     * another thread.
     * @throws InterruptedException
     */
    @Test
    public void concurrentHashMapIsThreadSafeTest() throws InterruptedException {
        final Map<Object, String> map = new ConcurrentHashMap<Object, String>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    hashMapThreadAwareTest(map);
                } catch (InterruptedException e) {
                    fail();
                }
            }
        }).start();
        countDownLatch.await(5, TimeUnit.SECONDS);
        assertThat(map.size(), is(1));
    }

    /**
     * The idea is the following:
     * 1. Create class that returns the same hashCode and executes "equals()"
     * function for a long time (using locks). Objects of this class will be used as keys.
     * 2. We add the first value into the map (equals() is not called)
     * 3. We add the second value to the map in a separate thread. As table in the same bucket is not empty
     * keys comparison will take place and "equals()" method will be called. Separate thread will "stuck" in
     * an infinitive loop. The previously added value will be in separate thread stack as an object to compare with.
     * 4. While separate thread executing "equals()" - we remove the first value from the map in the main thread.
     * 5. We release the separate thread, "equals()" function finish execution and returns true. In "put()" value
     * of the first and the second entries are swapped. But as the first value us already removed fom the map
     * (table in the map doesn't contain the link to the object any more) map will remain empty.
     * If operations were synced we will have map.size() = 1. But we have 0.
     * @param map
     * @throws InterruptedException
     */
    public void hashMapThreadAwareTest(final Map<Object, String> map) throws InterruptedException {
        final Lock lock = new ReentrantLock();
        final boolean[] waiting = new boolean[1];

        //Create a key class that wll always return the same hash code and "stuck" on equals() call
        class CorruptingHash{
            @Override
            public int hashCode() {
                return 1;
            }

            @Override
            public boolean equals(Object obj) {
                //Need a waiting lock so this thread will be executed BEFORE previous item was removed
                waiting[0] = false;

                //Wait until previous value is not removed
                while (!lock.tryLock()){
                    /* Do Nothing */
                }
                return true;
            }
        }

        //We create 2 keys - first will be removed from the hash
        CorruptingHash corruptingHashToRemove = new CorruptingHash();
        final CorruptingHash corruptingHash = new CorruptingHash();

        //Put the first value so that when we will add another one will get into the bucket lookup section of a hash map
        map.put(corruptingHashToRemove, "Value 1");

        //Create a lock to make "Add another value" thread "stuck" on equals() function
        lock.lock();

        //Create a boolean lock (accessed from inner class) to make main thread wait
        // until additional thread will start executing
        waiting[0] = true;

        //In this thread we will add a new value to the map
        //The thread will stuck on equals() call
        //Meanwhile we will remove the first item from the map table
        Thread corrupted = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Put value 2 started");
                map.put(corruptingHash, "Value 2");
                System.out.println("Put value 2 finished");
            }
        });
        corrupted.start();

        //Wait until the "currupted" thread is called
        while (waiting[0]){
            /* Do Nothing */
        }

        System.out.println("Remove value 1 started");
        //Remove first element while the second is being added
        map.remove(corruptingHashToRemove);
        System.out.println("Remove value 1 finished");

        //Release lock to continue put() execution in "corrupted" thread
        lock.unlock();

        //Wait until "corrupted" thread is finished
        corrupted.join();
    }

}
