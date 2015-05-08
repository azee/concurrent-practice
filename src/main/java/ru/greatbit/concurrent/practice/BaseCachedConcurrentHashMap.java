package ru.greatbit.concurrent.practice;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by azee on 02.05.15.
 */
public class BaseCachedConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V>{
    protected List<Entry<K, V>> values;
    Random random = new Random();

    public BaseCachedConcurrentHashMap(int initialCapacity) {
        super(initialCapacity);
        values = new ArrayList<>(initialCapacity);
    }

    public List<Entry<K, V>> selectRandomEntries(int size){
        List<Entry<K, V>> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(values.get(random.nextInt(size)));
        }
        return result;
    }
}
