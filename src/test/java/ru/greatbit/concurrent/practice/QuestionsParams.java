package ru.greatbit.concurrent.practice;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.*;

/**
 * iterating through ConcurrentHashMap takes O( n ) time.
 *
 * Modify or wrap ConcurrentHashMap to provide a means of selecting 15 random entries in O(1) time.
 *
 * The following code give you an idea of what is expected. * Created by gluck on 9/05/2014.
 */
@RunWith(value = Parameterized.class)
public class QuestionsParams {
    private static final int MAP_SIZE = 5_000;
    private int selectSize;
    private static CachedConcurrentHashMap<Integer, String> dataMap = new CachedConcurrentHashMap<>(MAP_SIZE);

    public QuestionsParams(int selectSize) {
        this.selectSize = selectSize;
    }

    @BeforeClass
    public static void populateDataMap() {
        for (int i = 0; i < MAP_SIZE; i++) {
            dataMap.put(i, "value" + i);
        }
    }

    @Test
    public void testFastConcurrentHashMap() {
        final long start = System.nanoTime();
        final Collection<Map.Entry<Integer, String>> selection = dataMap.selectRandomEntries(selectSize);
        assertThat(System.nanoTime() - start, lessThanOrEqualTo(TimeUnit.MICROSECONDS.toNanos(10 + 2 * selectSize)));
        assertEquals(selectSize, selection.size());
        for (Map.Entry<Integer, String> e : selection){
            assertTrue(dataMap.containsKey(e.getKey()));
        }
    }

    @Parameterized.Parameters
    @SuppressWarnings({"MethodWithMultipleReturnPoints",
            "RawUseOfParameterizedType",
            "IOResourceOpenedButNotSafelyClosed"})
    public static Collection<Object[]> data() {
        List<Object[]> params = new ArrayList<>();
        for (int i = 10; i <= 100; i++){
            params.add(new Object[]{i});
        }
        return params;
    }
}