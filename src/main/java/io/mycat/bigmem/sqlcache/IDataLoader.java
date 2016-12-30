package io.mycat.bigmem.sqlcache;

/**
 * Value 数据加载接口
 *
 * @author zagnix
 * @version 1.0
 * @create 2016-12-30 16:44
 */
public interface IDataLoader<K,V>{

    /**
     * 当访问的key,不存在时，回调该接口
     * @param key
     * @return
     */
    public V load(K key);

    /**
     * Key失效，时候重新reload数据
     * @param key
     * @return
     */
    public V reload(K key);
}
