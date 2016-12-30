package io.mycat.bigmem.sqlcache;

/**
 * key cache生效规则
 *
 * @author zagnix
 * @version 1.0
 * @create 2016-12-30 16:48
 */

public class Keyer<K,V> {
    private long cacheTTL;
    private long lastAccessTime;
    private long  refCount;
    private  K key;
    private  V value;

    private IDataLoader<K,V> iDataLoader;
    private RemoveKeyListener<K,V> removeKeyListener;

    public long getCacheTTL() {
        return cacheTTL;
    }

    public void setCacheTTL(long cacheTTL) {
        this.cacheTTL = cacheTTL;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public long getRefCount() {
        return refCount;
    }

    public void setRefCount(long refCount) {
        this.refCount = refCount;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public IDataLoader<K, V> getiDataLoader() {
        return iDataLoader;
    }

    public void setiDataLoader(IDataLoader<K, V> iDataLoader) {
        this.iDataLoader = iDataLoader;
    }

    public RemoveKeyListener<K, V> getRemoveKeyListener() {
        return removeKeyListener;
    }

    public void setRemoveKeyListener(RemoveKeyListener<K, V> removeKeyListener) {
        this.removeKeyListener = removeKeyListener;
    }
}
