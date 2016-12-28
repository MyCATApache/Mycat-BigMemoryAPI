package io.mycat.bigmem.sqlcache.impl.directmem;

import io.mycat.bigmem.sqlcache.IBigCache;

import java.io.IOException;

/**
 * 基于Direct Memory Cache,用queue实现
 *
 * @author zagnix
 * @create 2016-12-28 07:46
 */

public class DirectMemoryBigCache implements IBigCache {
    public long put(byte[] data) throws IOException {
        return 0;
    }

    public byte[] next() throws IOException {
        return new byte[0];
    }

    public byte[] get(long index) throws IOException {
        return new byte[0];
    }

    public long size() {
        return 0;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean isFull() {
        return false;
    }

    public void flush() {

    }

    public void removeAll() throws IOException {

    }

    public void recycle() throws IOException {

    }
}
