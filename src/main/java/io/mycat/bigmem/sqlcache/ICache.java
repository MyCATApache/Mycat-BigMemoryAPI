package io.mycat.bigmem.sqlcache;

/**
 * 定制化 Cache接口
 * 主要实现SQL结果集缓存规则
 * 1.缓存时间
 * 2.在缓存时间内，访问次数超过N次，重新从DB后台load数据，更新Value
 * or zagnix
 * @version 1.0
 *  2016-12-30 11:26
 */
public interface ICache<K, V> {

    /**
     * 放入
     * @param key 参数
     * @param value 值
     * @param keyer 参数
     */
    public void put(final K key,final V value,final Keyer<K,V> keyer);

    /**
     *
     * @param key 参数
     * @return 返回 
     */
    public V get(final K key);


    /**
     * 参数信息
     * @param key 参数信息
     */
    public void remove(final K key);

    /**
     *
     */
    public void removeALL();


}
