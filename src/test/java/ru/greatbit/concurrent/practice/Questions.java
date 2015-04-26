package ru.greatbit.concurrent.practice;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.Matchers.lessThan;
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
    //private static ConcurrentHashMap<Integer, String> dataMap = new ConcurrentHashMap<Integer, String>(size);
    private static ConcurrentHashMap<Integer, String> dataMap = new IntegerConcurrentHashMap<>(size);

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
        ArrayList<String> randomlySelectedEntries = new ArrayList<String>(15);

        Random random = new Random();

        long start = System.nanoTime();
        for (int i = 0; i < 15; i++) {
            int index = random.nextInt(size);
            randomlySelectedEntries.add(dataMap.get(index));
        }
        long end = System.nanoTime();
        assertThat(end - start, lessThan(35l * 1000L));
    }

//    @Test
//    public void testFastConcurrentHashMap() {
//        ArrayList<Map.Entry<Integer, String>> randomlySelectedEntries = new ArrayList<Map.Entry<Integer, String>>(15);
//
//        Random random = new Random();
//        for (int i = 0; i < 15; i++) {
//            int index = random.nextInt(size);
//            //modify ConcurrentHashMap to provide some past way of looking up by index;
//            // randomlySelectedEntries = someCodeYouWrote.get(index);
//            //randomlySelectedEntries.add(dataMap.get(index));
//        }
//    }
}