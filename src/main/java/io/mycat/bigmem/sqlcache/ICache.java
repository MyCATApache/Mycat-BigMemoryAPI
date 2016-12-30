package io.mycat.bigmem.sqlcache;

import java.util.concurrent.Callable;

/**
 * 定制化 Cache接口
 * 主要实现SQL结果集缓存规则
 * 1.缓存时间
 * 2.在缓存时间内，访问次数超过N次，重新从DB后台load数据，更新Value
 * @author zagnix
 * @version 1.0
 * @create 2016-12-30 11:26
 */
public interface ICache<K, V> {

    /**
     *
     *
     * @param key
     * @param value
     */
    public void put(final K key,final V value,final long ttl);
    /**
     *
     * @param key
     * @param value
     */
    public void put(final K key,final V value);


    /**
     *
     * @param key
     * @param callable
     */
    public void get(final K key,Callable<? extends V> callable);


    /**
     *
     * @param key
     */
    public void remove(final K key);

    /**
     *
     */
    public void removeALL();


}
