package io.mycat.bigmem.chunkpageallot.buffer.impl;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

import io.mycat.bigmem.chunkpageallot.buffer.MycatBuffer;
import io.mycat.bigmem.chunkpageallot.buffer.MycatBufferBase;
import io.mycat.bigmem.chunkpageallot.buffer.MycatMovableBufer;
import io.mycat.bigmem.chunkpageallot.console.BufferException;
import io.mycat.bigmem.util.UnsafeHelper;
import sun.misc.Unsafe;

/**
 * 进行直接内存的操作
* 源文件名：DirectMycatBufferImpl.java
* 文件版本：1.0.0
* 创建作者：liujun
* 创建日期：2016年12月22日
* 修改作者：liujun
* 修改日期：2016年12月22日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public class DirectMycatBufferMoveImpl extends MycatBufferBase implements MycatMovableBufer {

    /**
     * 用来进行自己内存管理的对象
    *  unsafe
    */
    private Unsafe unsafe;

    /**
     * 是否进行内存整理标识,默认为true，即允许进行整理
    *  clearFlag
    */
    private volatile boolean clearFlag = true;

    /**
     * 用来控制队列的访问，同一时间，不能被多个线程同同时操作队列
    *  lock
    */
    private Semaphore accessReq = new Semaphore(1);

    /**
     * 构造方法，进行内存容量的分配操作
    * 构造方法
    * @param memorySize 内存容量信息
    */
    public DirectMycatBufferMoveImpl(int memorySize) {
        // // 获得首地址信息
        unsafe = UnsafeHelper.getUnsafe();
        // 进行内存分配
        address = unsafe.allocateMemory(memorySize);
        // 设置所有的内存地址都为0
        unsafe.setMemory(address, memorySize, (byte) 0);
        // 设置limit以及空量信息
        this.limit = memorySize;
        // 设置容量
        this.capacity = memorySize;
    }

    public DirectMycatBufferMoveImpl(DirectMycatBufferMoveImpl dirbuffer, int position, int limit, long address) {
        this.putPosition = position;
        this.limit = limit;
        // 设置容量
        this.capacity = limit;
        this.address = address;
        this.att = dirbuffer;
        this.unsafe = dirbuffer.unsafe;
    }

    private long getIndex(long offset) {
        if (limit < offset)
            throw new BufferOverflowException();
        return address + offset;
    }

    @Override
    public void setByte(int offset, byte value) {

        // 验证当前是否在进行内存整理
        checkClearFlag();

        unsafe.putByte(getIndex(offset), value);
    }

    @Override
    public byte getByte(int offset) {

        // 验证当前是否在进行内存整理
        checkClearFlag();

        // 仅允许同一线程操作
        return unsafe.getByte(getIndex(offset));
    }

    @Override
    public void copyTo(ByteBuffer buffer) {

        // 验证当前是否在进行内存整理
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
        // 验证当前是否在进行内存整理
        checkClearFlag();
        // 修改当前的标识
        this.limit = this.putPosition;
    }

    @Override
    public MycatBufferBase slice() {

        // 验证当前是否在进行内存整理
        checkClearFlag();

        int currPosition = this.putPosition;
        int cap = this.limit - currPosition;
        long address = this.address + currPosition;
        // 生新新的引用对象
        return new DirectMycatBufferMoveImpl(this, 0, cap, address);
    }

    /**
     * 将添加的指针加1
    * 方法描述
    * @return
    *  2016年12月23日
    */
    private long addPutPos() {
        if (this.putPosition >= this.limit)
            throw new BufferOverflowException();
        return this.putPosition++;
    }

    /**
     * 将获取的指针加1
    * 方法描述
    * @return
    *  2016年12月23日
    */
    private long addGetPos() {
        if (this.getPosition > this.limit)
            throw new BufferOverflowException();
        return this.getPosition++;
    }

    @Override
    public MycatBuffer putByte(byte b) {

        // 验证当前内存整理标识
        checkClearFlag();

        unsafe.putByte(getIndex(this.addPutPos()), b);

        return this;
    }

    @Override
    public byte get() {

        // 验证当前内存整理标识
        checkClearFlag();

        return unsafe.getByte(getIndex(this.addGetPos()));

    }

    /**
     * 进行内存整理的标识验证
    * 方法描述
    *  2016年12月27日
    */
    private void checkClearFlag() {
        // 仅当不进行整理时，才能进行操作
        if (clearFlag) {
            throw new BufferException("DirectMycatBufferImpl exception,please invoke beginOp");
        }
    }

    public void beginOp() throws InterruptedException  {
        accessReq.acquire();
        // 标识当前正在进行内存操作，不能整理内存
        clearFlag = false;
    }

    @Override
    public void commitOp() {
        // 内存整理完毕可以进行内存整理
        clearFlag = true;
        // 访问结束，释放语可
        accessReq.release();
    }

    @Override
    public void limit(int limit) {

        // 验证当前内存整理标识
        checkClearFlag();

        this.limit = limit;
    }

    @Override
    public void putPosition(int position) {

        // 验证当前内存整理标识
        checkClearFlag();

        this.putPosition = position;
    }

    public void getPosition(int getPosition) {

        // 验证当前内存整理标识
        checkClearFlag();

        this.getPosition = getPosition;
    }

    @Override
    public boolean getClearFlag() {
        return this.clearFlag;
    }

    @Override
    public void memoryCopy(long srcAddress, long targerAddress, int length) {

        // 验证当前内存整理标识
        checkClearFlag();

        // 进行堆外的内存的拷贝操作
        unsafe.copyMemory(null, srcAddress, null, targerAddress, length);

    }

}
