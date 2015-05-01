package ru.greatbit.concurrent.practice;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by azee on 02.05.15.
 */
public class BaseIntegerConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V>{
    protected List<V> values;

    public BaseIntegerConcurrentHashMap(int initialCapacity) {
        super(initialCapacity);
        values = new ArrayList<>(initialCapacity);
    }

    public List<V> values(List<Integer> indexes){
        List<V> result = new LinkedList<>();
        for (Integer index : indexes){
            result.add(values.get(index));
        }
        return result;
    }
}
