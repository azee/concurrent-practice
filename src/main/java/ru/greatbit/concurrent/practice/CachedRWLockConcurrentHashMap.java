package ru.greatbit.concurrent.practice;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by azee on 25.04.15.
 */
public class CachedRWLockConcurrentHashMap<K, V> extends BaseCachedConcurrentHashMap<K, V> {

    ReadWriteLock lock = new ReentrantReadWriteLock();

    public CachedRWLockConcurrentHashMap(int initialCapacity) {
        super(initialCapacity);
        values = new ArrayList<>(initialCapacity);
    }

    @Override
    public V put(K key, V value) {
        lock.writeLock().lock();
        values.add((Integer)key, new SimpleEntry<K, V>(key, value));
        lock.writeLock().unlock();
        return super.put(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        V previous = super.putIfAbsent(key, value);
        if (previous == null){
            lock.writeLock().lock();
            values.add(new SimpleEntry<K, V>(key, value));
            lock.writeLock().unlock();
        }
        return previous;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        super.putAll(m);
        lock.writeLock().lock();
        for (K key : m.keySet()){
            values.add(new SimpleEntry<K, V>(key, m.get(key)));
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
            values.add(new SimpleEntry<K, V>(key, newValue));
            lock.writeLock().unlock();
        }
        return replaced;
    }

    @Override
    public V replace(K key, V value) {
        lock.writeLock().lock();
        values.add((Integer) key, new SimpleEntry<K, V>(key, value));
        lock.writeLock().unlock();
        return super.replace(key, value);
    }
}
