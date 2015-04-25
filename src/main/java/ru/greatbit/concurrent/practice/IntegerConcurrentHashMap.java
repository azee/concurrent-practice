package ru.greatbit.concurrent.practice;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by azee on 25.04.15.
 */
public class IntegerConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {
    private volatile List<V> values;


    public IntegerConcurrentHashMap(int initialCapacity) {
        super(initialCapacity);
        values = new ArrayList<>(initialCapacity);
    }

    @Override
    public V put(K key, V value) {
        verifyKey(key);
        synchronized (this){
            values.add((Integer)key, value);
        }
        return super.put(key, value);
    }

    @Override
    public V get(Object key) {
        return values.get((Integer) verifyKey(key));
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return super.entrySet();
    }

    private Object verifyKey(Object key){
        if (!(key instanceof Integer)){
            throw new UnsupportedOperationException("Integer Hash Map supports only Integer keys");
        }
        return key;
    }

}
