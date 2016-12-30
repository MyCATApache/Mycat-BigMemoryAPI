package io.mycat.bigmem.sqlcache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * ICache实现类
 *
 * @author zagnix
 * @version 1.0
 * @create 2016-12-30 16:53
 */

public class CacheImp<K,V> implements ICache<K,V> {

    private final static Logger logger
            = LoggerFactory.getLogger(CacheImp.class);

    public static final long DEFAULT_TTL = 5 * 1000;

    private final Map<K,V> map;
    private final Map<K,Keyer<K,V>> key;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public CacheImp(){
        map = new HashMap<K, V>();
        key = new HashMap<K, Keyer<K,V>>();
    }

    @Override
    public void put(K key, V value, long ttl) {

    }

    @Override
    public void put(K key, V value) {

    }

    @Override
    public void get(K key, Callable<? extends V> callable) {

    }

    @Override
    public void remove(K key) {

    }

    @Override
    public void removeALL() {

    }
}
