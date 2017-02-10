package io.mycat.bigmem.chunkpageallot.bufferpage.impl;

import io.mycat.bigmem.chunkpageallot.buffer.MycatBufferBase;
import io.mycat.bigmem.chunkpageallot.buffer.MycatMovableBufer;
import io.mycat.bigmem.chunkpageallot.bufferpage.BufferPageBase;

/**
 * 缓冲内存页的数据
* 源文件名：BufferPage.java
* 文件版本：1.0.0
* 创建作者：liujun
* 创建日期：2016年12月27日
* 修改作者：liujun
* 修改日期：2016年12月27日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public class FileMapBufferPage extends BufferPageBase {

    /**
    * 构造方法
    * @param memorySize
    * @param chunkSize
    */
    public FileMapBufferPage(MycatBufferBase buffer, int chunkSize) {
        // 进行父类的引用
        super(buffer, chunkSize);
    }

    /**
    * 检查当前内存页能否满足内存数据的分配要求
    * 方法描述
    * @param chunkNum
    * @return 1,可分配 0，不能
    * @创建日期 2016年12月19日
    */
    public boolean checkNeedChunk(int chunkNum) {
        // 仅在未锁定的情况下，才进行检查
        if (!isLock.get()) {
            // 如果当前可分配的内存块满足要求
            if (this.canUseChunkNum >= chunkNum) {
                return true;
            }
        }
        return false;
    }

    /**
    * 获得chunk的buffer信息
    * 方法描述
    * @param needChunkSize
    * @param timeout 系统过期时间
    * @return
    * @创建日期 2016年12月19日
    */
    public MycatBufferBase alloactionMemory(int needChunkSize) {
        // 如果当前的可分配的内在块小于需要内存块，则返回
        if (canUseChunkNum < needChunkSize) {
            return null;
        }

        // 如果当前不能加锁成功，则返回为null
        if (!isLock.compareAndSet(false, true)) {
            return null;
        }

        try {

            int startIndex = -1;
            int endIndex = 0;
            // 找到当前连续未使用的内存块
            for (int i = 0; i < chunkCount; i++) {
                if (memUseSet.get(i) == false) {
                    if (startIndex == -1) {
                        startIndex = i;
                        endIndex = 1;
                        // 只需要1时，进不需再进行遍历
                        if (needChunkSize == 1) {
                            break;
                        }
                    } else {
                        if (++endIndex == needChunkSize) {
                            break;
                        }
                    }
                }
                // 如果已经使用，则标识当前已经使用，则从已经使用的位置开始
                else {
                    startIndex = -1;
                    endIndex = 0;
                }
            }

            // 如果找到适合的内存块大小
            if (endIndex == needChunkSize) {
                // 将这一批数据标识为已经使用
                int needChunkEnd = startIndex + needChunkSize;
                memUseSet.set(startIndex, needChunkEnd);

                MycatMovableBufer moveBuffer = null;

                // 检查当前对象是否实现了可移动接口
                if (buffer instanceof MycatMovableBufer) {
                    moveBuffer = (MycatMovableBufer) buffer;

                    // 标识为不可移动
                    try {
                        moveBuffer.beginOp();

                        // 标识开始与结束号
                        buffer.putPosition(startIndex * chunkSize);
                        buffer.limit(needChunkEnd * chunkSize);

                        // 进行数据进行匹配分段操作
                        MycatBufferBase bufferResult = buffer.slice();

                        // 当前可使用的，为之前的结果前去当前的需要的，
                        canUseChunkNum = canUseChunkNum - needChunkSize;

                        return bufferResult;
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally {
                        // 标识当前操作完成
                        moveBuffer.commitOp();
                    }
                }

            } else {
                return null;
            }

        } finally {
            isLock.set(false);
        }

        return null;
    }

    /**
    * 进行内存的归还操作，以便后面再使用
    * 方法描述
    * @param parentBuffer
    * @param chunkStart
    * @param chunkNum
    * @创建日期 2016年12月19日
    */
    public boolean recycleBuffer(MycatBufferBase parentBuffer, int chunkStart, int chunkNum) {
        if (this.buffer == parentBuffer) {
            // 如果加锁失败，则执行其他代码
            if (!isLock.compareAndSet(false, true)) {
                Thread.yield();
            }

            try {
                int endChunkNum = chunkStart + chunkNum;
                // 将当前指定的内存块归还
                memUseSet.clear(chunkStart, endChunkNum);

                // 归还了内存，则需要将可使用的内存加上归还的内存
                this.canUseChunkNum = canUseChunkNum + chunkNum;

            } finally {
                isLock.set(false);
            }

            return true;

        }

        return false;
    }

    // public static void main(String[] args) {
    // MycatMovableBufer buffer = new DirectMycatBufferImpl(2048);
    //
    // UnsafeDirectBufferPage page = new UnsafeDirectBufferPage(buffer, 256);
    //
    // buffer.beginOp();
    //
    // MycatBuffer buffer1 = page.alloactionMemory(4);
    // MycatBuffer buffer2 = page.alloactionMemory(4);
    //
    // buffer.commitOp();
    // // // 获得内存buffer
    // // sun.nio.ch.DirectBuffer thisNavBuf = (sun.nio.ch.DirectBuffer)
    // // bufferItem2;
    // // // attachment对象在buf.slice();的时候将attachment对象设置为总的buff对象
    // // sun.nio.ch.DirectBuffer parentBuf = (sun.nio.ch.DirectBuffer)
    // // thisNavBuf.attachment();
    // // //
    // // 已经使用的地址减去父类最开始的地址，即为所有已经使用的地址，除以chunkSize得到chunk当前开始的地址,得到整块内存开始的地址
    // // int startChunk = (int) ((page - parentBuf.address()) / 256);
    // //
    // // // 归还当前buffer2的内存
    // // page.recycleBuffer(parentBuf, startChunk, 2);
    // //
    // // System.out.println(bufferItem);
    // // System.out.println(bufferItem2);
    // // System.out.println(bufferItem3);
    //
    // }

}
