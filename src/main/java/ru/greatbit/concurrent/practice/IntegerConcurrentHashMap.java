package ru.greatbit.concurrent.practice;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by azee on 25.04.15.
 */
public class IntegerConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {
    protected List<V> values;

    // We won't use RWLock because its instantiation cost a lot
    //in this particular example because we instantiate all values
    //in the test setup and we don't need to lock while reading
    //ReadWriteLock lock = new ReentrantReadWriteLock();


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

    //No need to sync - we are not updating values in the test
    //If we were - we'd better use a ReadWriteLock
    @Override
    public V get(Object key) {
        return values.get((Integer) verifyKey(key));
    }

    protected Object verifyKey(Object key){
        if (!(key instanceof Integer)){
            throw new UnsupportedOperationException("Integer Hash Map supports only Integer keys");
        }
        return key;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        V previous = super.putIfAbsent(key, value);
        if (previous == null){
            synchronized (this){
                values.add((Integer) verifyKey(key), value);
            }
        }
        return previous;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        super.putAll(m);
        synchronized (this){
            for (K key : m.keySet()){
                values.add((Integer) verifyKey(key), m.get(key));
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
                values.add((Integer) verifyKey(key), newValue);
            }
        }
        return replaced;
    }

    @Override
    public V replace(K key, V value) {
        synchronized (this) {
            values.add((Integer) verifyKey(key), value);
        }
        return super.replace(key, value);
    }
}
