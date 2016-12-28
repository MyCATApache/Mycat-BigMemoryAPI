package io.mycat.bigmem.sqlcache;

import java.io.IOException;

/**
 * 管理 Buffer Page Factory 接口
 * @author zagnix
 * @create 2016-11-18 16:46
 */
public interface IBigCache {
    public long put(byte[] data) throws IOException;
    public byte[] next() throws IOException;
    public byte[] get(long index) throws IOException;
    public long size();
    public boolean isEmpty() ;
    public boolean isFull();
    public void flush();
    public void removeAll() throws IOException;
    public void recycle() throws IOException;
}
