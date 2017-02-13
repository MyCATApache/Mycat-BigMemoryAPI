package io.mycat.bigmem.sqlcache;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Cache Page接口
 *
 * or zagnix
 * @version 1.0
 *  2016-12-27 18:49
 */

public interface IBufferPage {

    /**
     *  get bytes from Thread Local
     *
     * @param position 参数
     * @param length 参数
     * @return 参数
     */
    public byte[] getBytes(int position, int length);

    /**
     *  ByteBuffer slice from Thread Local
     *
     * @param position 参数
     * @param limit 参数
     * @return 参数
     */
    public ByteBuffer slice(int position, int limit);

    /**
     * get ByteBuffer from Thread Local
     *
     * @param position 参数
     * @return 参数
     */
    public ByteBuffer getLocalByteBuffer(int position);


    /**
     * 将数据刷到磁盘
     */
    public void flush();


    /**
     * 页号
     *
     * @return 返回
     */
    long getPageIndex();


    /**
     * 设置页有数据被写入了，属于’脏页‘
     *
     * @param dirty 参数
     */
    void setDirty(boolean dirty);

    /**
     * 页回收
     * @throws IOException 异常
     */
    public void recycle() throws IOException;

    /**
     * 页是否被回收了
     *
     * @return 返回
     */
    boolean isRecycled();
}
