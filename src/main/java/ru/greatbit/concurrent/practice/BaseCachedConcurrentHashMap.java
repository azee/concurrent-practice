package ru.greatbit.concurrent.practice;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by azee on 02.05.15.
 */
public class BaseCachedConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V>{
    protected List<V> values;

    public BaseCachedConcurrentHashMap(int initialCapacity) {
        super(initialCapacity);
        values = new ArrayList<>(initialCapacity);
    }

    public List<Entry<K, V>> values(List<Integer> indexes){
        List<Entry<K, V>> result = new LinkedList<>();
        for (Integer index : indexes){
            result.add(new SimpleEntry<K, V>((K) index, values.get(index)));
        }
        return result;
    }

}
