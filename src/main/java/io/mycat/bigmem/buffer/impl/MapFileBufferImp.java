package io.mycat.bigmem.buffer.impl;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.Semaphore;

import io.mycat.bigmem.buffer.CatCallbackInf;
import io.mycat.bigmem.buffer.MycatBuffer;
import io.mycat.bigmem.buffer.MycatBufferBase;
import io.mycat.bigmem.buffer.MycatMovableBufer;
import io.mycat.bigmem.buffer.MycatSwapBufer;
import io.mycat.bigmem.console.BufferException;
import io.mycat.bigmem.threadpool.ThreadPool;
import io.mycat.bigmem.util.IOutils;
import io.mycat.bigmem.util.UnsafeHelper;
import sun.misc.Unsafe;
import sun.nio.ch.FileChannelImpl;

/**
 * 文件映射的buffer的实现
* 源文件名：MapFileBufferImp.java
* 文件版本：1.0.0
* 创建作者：Think
* 创建日期：2016年12月25日
* 修改作者：Think
* 修改日期：2016年12月25日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
@SuppressWarnings("restriction")
public class MapFileBufferImp extends MycatBufferBase implements MycatSwapBufer, MycatMovableBufer {

    /**
     * 内存控制的对象信息 
    * @字段说明 unsafe
    */
    public static Unsafe unsafe;

    /**
     * 获得内存映射的方法
    * @字段说明 mmap
    */
    public static final Method mmap;

    /**
    * 解除映射的方法
    * @字段说明 unmmap
    */
    public static final Method unmmap;

    // /**
    // * byte的内存的固定的偏移
    // * @字段说明 BYTE_ARRAY_OFFSET
    // */
    // public static final int BYTE_ARRAY_OFFSET;

    /**
     * 默认文件大小为128M
     */
    private int fileSize = 1024 * 1024 * 128;

    /**
     * 文件名称
    * @字段说明 fileName
    */
    private final String fileName;

    /**
     * 随机文件读写信息
    * @字段说明 randomFile
    */
    private RandomAccessFile randomFile;

    /**
     * 文件通道信息
    * @字段说明 channel2
    */
    private FileChannel channel;

    /**
     * 是否进行内存整理标识,默认为true，即允许进行整理
    * @字段说明 clearFlag
    */
    private volatile boolean clearFlag = true;

    /**
     * 用来控制队列的访问，同一时间，不能被多个线程同同时操作队列
    * @字段说明 lock
    */
    private Semaphore accessReq = new Semaphore(1);

    static {
        try {
            Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
            singleoneInstanceField.setAccessible(true);
            unsafe = (Unsafe) singleoneInstanceField.get(null);
            mmap = getMethod(FileChannelImpl.class, "map0", int.class, long.class, long.class);
            mmap.setAccessible(true);

            unmmap = getMethod(FileChannelImpl.class, "unmap0", long.class, long.class);
            unmmap.setAccessible(true);
            // BYTE_ARRAY_OFFSET = unsafe.arrayBaseOffset(byte[].class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MapFileBufferImp(int size) throws IOException {
        String path = MapFileBufferImp.class.getClassLoader().getResource("mapfile").getPath();

        fileName = path + "/mapFile-" + System.nanoTime() + ".txt";

        // // 获得首地址信息
        unsafe = UnsafeHelper.getUnsafe();

        randomFile = new RandomAccessFile(fileName, "rw");

        // 设置文件大小
        randomFile.setLength(this.fileSize);
        channel = randomFile.getChannel();

        // 获得内存映射的地地址
        try {
            address = (long) mmap.invoke(channel, 1, 0, size);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IOException(e);
        }

        // 设置容量相关的东西
        this.limit = this.fileSize;
        this.capacity = this.fileSize;
    }

    public MapFileBufferImp(MapFileBufferImp dirbuffer, int position, int limit, long address) {
        this.putPosition = position;
        this.limit = limit;
        // 设置容量
        this.capacity = limit;
        this.address = address;
        this.att = dirbuffer;
        // 设置文件名称
        this.fileName = dirbuffer.fileName;
        // 文件流信息
        this.randomFile = dirbuffer.randomFile;
        // 通道信息
        this.channel = dirbuffer.channel;
    }

    /**
     * 获取写入的索引编号
    * 方法描述
    * @param offset
    * @return
    * @创建日期 2016年12月24日
    */
    private long getIndex(long offset) {
        if (limit < offset)
            throw new BufferOverflowException();
        return address + offset;
    }

    /**
     * 将添加的指针加1
    * 方法描述
    * @return
    * @创建日期 2016年12月23日
    */
    private long addPutPos() {
        if (this.putPosition > this.limit)
            throw new BufferOverflowException();
        return this.putPosition++;
    }

    /**
     * 将获取的指针加1
    * 方法描述
    * @return
    * @创建日期 2016年12月23日
    */
    private long addGetPos() {
        if (this.getPosition > this.limit)
            throw new BufferOverflowException();
        return this.getPosition++;
    }

    @Override
    public void setByte(int offset, byte value) {
        // 验证当前内存整理标识
        checkClearFlag();

        // 获取文件的游标
        int position = 0;
        try {
            position = (int) channel.position();

            // 进行内存数据写入
            unsafe.putByte(address + offset + position, value);
        } catch (IOException e) {
            throw new BufferException("MapFileBufferImp setByte Exception ", e);
        }

    }

    @Override
    public MycatBuffer putByte(byte b) {

        // 验证当前内存整理标识
        checkClearFlag();

        // 获取文件的游标
        int filePosition = 0;
        try {
            filePosition = (int) channel.position();

            // 计算写入的游标
            long currPostision = addPutPos();
            // 进行内存数据写入
            unsafe.putByte(address + currPostision, b);
            // 将新的文件游标写入到文件中,当前为写入单byte文件
            channel.position(filePosition + 1);
        } catch (IOException e) {
            throw new BufferException("MapFileBufferImp put Exception ", e);
        }

        return this;
    }

    @Override
    public byte getByte(int offset) {
        // 验证当前内存整理标识
        checkClearFlag();

        return unsafe.getByte(getIndex(offset));
    }

    @Override
    public byte get() {
        // 验证当前内存整理标识
        checkClearFlag();

        return unsafe.getByte(getIndex(addGetPos()));
    }

    @Override
    public void copyTo(ByteBuffer buffer) {
        // 验证当前内存整理标识
        checkClearFlag();

        if (buffer.capacity() < this.limit) {
            throw new BufferOverflowException();
        }
        // 获取当前堆外的内存的地址
        long buffAddress = ((sun.nio.ch.DirectBuffer) buffer).address();
        // 进行内存的拷贝
        unsafe.copyMemory(null, address, null, buffAddress, this.limit);
    }

    @Override
    public void recycleUnuse() {

        // 验证当前内存整理标识
        checkClearFlag();

        this.limit(this.putPosition);

        try {
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MycatBufferBase slice() {
        // 验证当前内存整理标识
        checkClearFlag();

        int currPosition = this.getPosition;
        int cap = this.limit - currPosition;
        long address = this.address + currPosition;
        // 生新新的引用对象
        return new MapFileBufferImp(this, 0, cap, address);
    }

    private static Method getMethod(Class<?> cls, String name, Class<?>... params) throws Exception {
        Method m = cls.getDeclaredMethod(name, params);
        m.setAccessible(true);
        return m;
    }

    private void unmap() throws Exception {
        unmmap.invoke(null, address, this.capacity);
    }

    @Override
    public void swapln() throws IOException {

        // 验证当前内存整理标识
        checkClearFlag();

        // 重新加载文件
        randomFile = new RandomAccessFile(fileName, "rw");

        // 设置文件大小
        randomFile.setLength(this.fileSize);
        channel = randomFile.getChannel();

        // 重新获取内存的地址信息
        try {
            address = (long) mmap.invoke(channel, 1, 0, this.capacity);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IOException(e);
        }

        // 设置容量相关的东西
        this.limit = this.fileSize;
        // 加载后设置两个指针
        this.putPosition = (int) channel.position();
        this.getPosition = 0;

    }

    @Override
    public void swapOut() {

        // 验证当前内存整理标识
        checkClearFlag();

        // 关闭流将文件刷入磁盘中
        IOutils.closeStream(channel);
        IOutils.closeStream(randomFile);

        // 将内存释放
        try {
            this.unmap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 标识空间为0
        this.limit = 0;
        // 设置两个指针
        this.putPosition = 0;
        this.getPosition = 0;
    }

    @Override
    public void swapIn(CatCallbackInf notify) throws IOException {

        // 验证当前内存整理标识
        checkClearFlag();

        // 将文件重新加载映射到内存中
        this.swapln();
        // 进行异步的通知
        this.callBackDoIt(notify);

    }

    private void callBackDoIt(final CatCallbackInf back) {

        // 进行异步的通知调用,使用jdk8的特性直接执行回调函数
        ThreadPool.Instance().submit(() -> {
            try {
                back.callBack();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void swapOut(CatCallbackInf notify) {

        // 验证当前内存整理标识
        checkClearFlag();

        // 首先执行数据加载
        this.swapOut();
        // 进行通知操作
        this.callBackDoIt(notify);

    }

    /**
     * 进行内存整理的标识验证
    * 方法描述
    * @创建日期 2016年12月27日
    */
    private void checkClearFlag() {
        // 仅当不进行整理时，才能进行操作
        if (clearFlag) {
            throw new BufferException("MapFileBufferImp exception,please invoke beginOp");
        }
    }

    @Override
    public void beginOp() {
        try {
            // 获取语可
            accessReq.acquire();
            // 标识当前不能被内存管理器所移动
            clearFlag = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void commitOp() {
        // 操作完成，将内存管理器标识为可移动
        clearFlag = true;
        // 释放语可，允许其他在排除的线程进行调用
        accessReq.release();
    }

    @Override
    public boolean getClearFlag() {
        return clearFlag;
    }

    /**
     * 获取文件大小的方法
     * @return
     */
    public int getFileSize() {
        return fileSize;
    }

    /**
     * 设置文件大小的方法
     * @param fileSize
     */
    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

}
