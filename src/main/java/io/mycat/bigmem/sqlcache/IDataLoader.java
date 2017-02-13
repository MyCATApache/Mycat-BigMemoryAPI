package io.mycat.bigmem.sqlcache;

/**
 * Value 数据加载接口
 *
 * or zagnix
 * @version 1.0
 *  2016-12-30 16:44
 */
public interface IDataLoader<K,V>{

    /**
     * Key失效，时候重新异步reload数据
     * @param keyer 参数
     * @return 返回
     */
    public V reload(Keyer<K,V> keyer);
}
