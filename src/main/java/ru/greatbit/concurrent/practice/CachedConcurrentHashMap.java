package ru.greatbit.concurrent.practice;

import java.util.*;

/**
 * Created by azee on 25.04.15.
 */
public class CachedConcurrentHashMap<K, V> extends BaseCachedConcurrentHashMap<K, V> {

    public CachedConcurrentHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public V put(K key, V value) {
        synchronized (this){
            values.add((Integer)key, value);
        }
        return super.put(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        V previous = super.putIfAbsent(key, value);
        if (previous == null){
            synchronized (this){
                values.add(value);
            }
        }
        return previous;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        super.putAll(m);
        synchronized (this){
            for (K key : m.keySet()){
                values.add(m.get(key));
            }
        }
    }

    @Override
    public V remove(Object key) {
        synchronized (this){
            values.remove(key);
        }
        return super.remove(key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        synchronized (this){
            values.remove(key);
        }
        return super.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        boolean replaced = super.replace(key, oldValue, newValue);
        if (replaced) {
            synchronized (this) {
                values.add(newValue);
            }
        }
        return replaced;
    }

    @Override
    public V replace(K key, V value) {
        synchronized (this) {
            values.add(value);
        }
        return super.replace(key, value);
    }

}
