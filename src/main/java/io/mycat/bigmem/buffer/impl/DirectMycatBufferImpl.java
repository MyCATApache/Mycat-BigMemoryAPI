package io.mycat.bigmem.buffer.impl;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import io.mycat.bigmem.buffer.MycatBuffer;
import io.mycat.bigmem.buffer.MycatBufferBase;
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
public class DirectMycatBufferImpl extends MycatBufferBase {

    /**
     * 用来进行自己内存管理的对象
    * @字段说明 unsafe
    */
    private Unsafe unsafe;

    /**
     * 构造方法，进行内存容量的分配操作
    * 构造方法
    * @param memorySize 内存容量信息
    */
    public DirectMycatBufferImpl(int memorySize) {
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

    public DirectMycatBufferImpl(DirectMycatBufferImpl dirbuffer, int position, int limit, long address) {
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

        unsafe.putByte(getIndex(offset), value);
    }

    @Override
    public byte getByte(int offset) {

        // 仅允许同一线程操作
        return unsafe.getByte(getIndex(offset));
    }

    @Override
    public void copyTo(ByteBuffer buffer) {

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
        // 修改当前的标识
        this.limit = this.putPosition;
    }

    @Override
    public MycatBufferBase slice() {

        int currPosition = this.getPosition;
        int cap = this.limit - currPosition;
        long address = this.address + currPosition;
        // 生新新的引用对象
        return new DirectMycatBufferImpl(this, 0, cap, address);
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
    public MycatBuffer putByte(byte b) {

        unsafe.putByte(getIndex(this.addPutPos()), b);

        return this;
    }

    @Override
    public byte get() {

        return unsafe.getByte(getIndex(this.addGetPos()));

    }

    @Override
    public void beginOp() {

    }

    @Override
    public void commitOp() {

    }

}
