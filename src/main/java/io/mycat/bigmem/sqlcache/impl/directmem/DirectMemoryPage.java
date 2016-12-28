package io.mycat.bigmem.sqlcache.impl.directmem;

import io.mycat.bigmem.sqlcache.MyCatBufferPage;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 直接内存 off-heap 存储data
 *
 * @author zagnix
 * @version 1.0
 * @create 2016-12-27 18:53
 */

public class DirectMemoryPage extends MyCatBufferPage{

    public DirectMemoryPage(ByteBuffer bbufer, long cacheTTL){
        super(bbufer,cacheTTL);
    }
    public byte[] getBytes(int position, int length) {
        return new byte[0];
    }
    public ByteBuffer slice(int position, int limit) {
        return null;
    }

    public ByteBuffer getLocalByteBuffer(int position) {
        return null;
    }
    public void flush() {}
    public long getPageIndex() {
        return 0;
    }

    public void recycle() throws IOException {

    }
}
