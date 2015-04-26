package ru.greatbit.concurrent.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by azee on 25.04.15.
 */
public class IntegerRWLockConcurrentHashMap<K, V> extends IntegerConcurrentHashMap<K, V> {

    ReadWriteLock lock = new ReentrantReadWriteLock();

    public IntegerRWLockConcurrentHashMap(int initialCapacity) {
        super(initialCapacity);
        values = new ArrayList<>(initialCapacity);
    }

    @Override
    public V put(K key, V value) {
        verifyKey(key);
        lock.writeLock().lock();
        values.add((Integer)key, value);
        lock.writeLock().unlock();
        return super.put(key, value);
    }

    //No need to sync - we are not updating values in the test
    //If we were - we'd better use a ReadWriteLock
    @Override
    public V get(Object key) {
        lock.readLock().lock();
        try {
            return values.get((Integer) verifyKey(key));
        } finally {
            lock.readLock().unlock();
        }

    }

    @Override
    public V putIfAbsent(K key, V value) {
        V previous = super.putIfAbsent(key, value);
        if (previous == null){
            lock.writeLock().lock();
            values.add((Integer) verifyKey(key), value);
            lock.writeLock().unlock();
        }
        return previous;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        super.putAll(m);
        lock.writeLock().lock();
        for (K key : m.keySet()){
            values.add((Integer) verifyKey(key), m.get(key));
        }
        lock.writeLock().unlock();
    }

    @Override
    public V remove(Object key) {
        lock.writeLock().lock();
        values.remove(key);
        lock.writeLock().unlock();
        return super.remove(key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        lock.writeLock().lock();
        values.remove(key);
        lock.writeLock().unlock();
        return super.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        boolean replaced = super.replace(key, oldValue, newValue);
        if (replaced) {
            lock.writeLock().lock();
            values.add((Integer) verifyKey(key), newValue);
            lock.writeLock().unlock();
        }
        return replaced;
    }

    @Override
    public V replace(K key, V value) {
        lock.writeLock().lock();
        values.add((Integer) verifyKey(key), value);
        lock.writeLock().unlock();
        return super.replace(key, value);
    }
}
