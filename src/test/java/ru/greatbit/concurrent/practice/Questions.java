package ru.greatbit.concurrent.practice;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * iterating through ConcurrentHashMap takes O( n ) time.
 *
 * Modify or wrap ConcurrentHashMap to provide a means of selecting 15 random entries in O(1) time.
 *
 * The following code give you an idea of what is expected. * Created by gluck on 9/05/2014.
 */
public class Questions {
    private static final int size = 20 * 1000 * 1000;
    private static IntegerConcurrentHashMap<Integer, String> dataMap = new IntegerConcurrentHashMap<>(size);

    @BeforeClass
    public static void populateDataMap() {
        for (int i = 0; i < size; i++) {
            dataMap.put(i, "some data");
        }
    }

    /**
     * This code should execute in less than a few microseconds, and the time should not
     * go up if we increase size. i.e. O(1);
     */
    @Test
    public void testFastConcurrentHashMap() {
        List<String> randomlySelectedEntries = new ArrayList<String>(15);
        List<Integer> indexes = new ArrayList<>(15);
        Random random = new Random();
        for (int i = 0; i < 15; i++) {
            indexes.add(random.nextInt(size));
        }

        long start = System.nanoTime();
        randomlySelectedEntries = dataMap.values(indexes);
        long end = System.nanoTime();
        assertThat(end - start, lessThan(65l * 1000L));

        //Use randomlySelectedEntries so that compiler won't eliminate previous block
        //as optimisation
        assertThat(randomlySelectedEntries.size(), is(15));
    }
}