package io.mycat.bigmem.sqlcache;

/**
 * key 被移除时，回调该接口
 *
 * or zagnix
 * @version 1.0
 *  2016-12-30 16:47
 */
public interface IRemoveKeyListener<K,V> {

    /**
     * 当Key被移除时，回调该接口
     *
     * @param key 参数
     * @param value 参数
     */
    public void  removeNotify(K key,V value);
}
