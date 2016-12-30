package io.mycat.bigmem.cacheway.alloctor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import io.mycat.bigmem.buffer.DirectMemAddressInf;
import io.mycat.bigmem.buffer.MycatBufferBase;
import io.mycat.bigmem.cacheway.MemoryAlloctorInf;
import io.mycat.bigmem.cacheway.alloctor.directmem.DirectBufferPage;
import io.mycat.bigmem.console.ChunkMemoryAllotEnum;
import io.mycat.bigmem.console.LocatePolicy;

/**
 * java 内存池的实现
 * 源文件名：MemoryPool.java
 * 文件版本：1.0.0
 * 创建作者：liujun
 * 创建日期：2016年12月19日
 * 修改作者：liujun
 * 修改日期：2016年12月19日
 * 文件描述：TODO
 * 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
 */
public class MycatMemoryAlloctor implements MemoryAlloctorInf {

    /**
     * 存储级别的map
     */
    private static Set<ChunkMemoryAllotEnum> MEMORY_LEVEL = new TreeSet<>((o1, o2) -> {
        if (o1.getLevel() < o2.getLevel()) {
            return 1;
        } else if (o1.getLevel() > o2.getLevel()) {
            return -1;
        }
        return 0;
    });

    /**
    * 用来构建内存池对象信息
    * 构造方法
    * @param chunkSize
    * @param memorySize
    * @param poolSize
     * @throws IOException 
    */
    public MycatMemoryAlloctor() throws IOException {
        int msize = 1024 * 1024;

        for (int i = 0; i < ChunkMemoryAllotEnum.values().length; i++) {
            MEMORY_LEVEL.add(ChunkMemoryAllotEnum.values()[i]);
        }

        // 构建文件映射的相关初始化
        ChunkMemoryAllotEnum.MEMORY_MAPFILE.getChunkAllot().allotorInit(msize * 1024, 4096, (short) 4);
        // 可移动的内存文件块的构建
        ChunkMemoryAllotEnum.MEMORY_DIRECT_MOVE.getChunkAllot().allotorInit(msize * 512, 4096, (short) 4);
        // 进行不可移动的内存块的构建
        ChunkMemoryAllotEnum.MEMORY_DIRECT.getChunkAllot().allotorInit(msize * 1024, 4096, (short) 4);
    }

    /**
     * 获取最高级别，也就是最先匹配的级别
     * @param allocFlag
     * @param minlevel
     * @return
     */
    private ChunkMemoryAllotEnum memoryMatchlevel(int allocFlag, int index) {

        ChunkMemoryAllotEnum chunkAllot = null;

        for (ChunkMemoryAllotEnum chunkMemoryAllotEnum : MEMORY_LEVEL) {
            // 首次需要匹配上最优的内存内存操作
            if (index == 0) {
                // 检查是否有当前的级别
                if ((chunkMemoryAllotEnum.getLevel() & allocFlag) == chunkMemoryAllotEnum.getLevel()) {
                    chunkAllot = chunkMemoryAllotEnum;
                    index = chunkMemoryAllotEnum.getMoveBit();
                    break;
                }
            } else {
                // 按级别选择最优的内存
                if (chunkMemoryAllotEnum.getMoveBit() < index
                        && (chunkMemoryAllotEnum.getLevel() & allocFlag) == chunkMemoryAllotEnum.getLevel()) {
                    chunkAllot = chunkMemoryAllotEnum;
                    index = chunkMemoryAllotEnum.getMoveBit();
                }
            }
        }

        return chunkAllot;
    }

    public static void main(String[] args) {

        Set<ChunkMemoryAllotEnum> MEMORY_LEVEL = new TreeSet<>((o1, o2) -> {
            if (o1.getLevel() < o2.getLevel()) {
                return 1;
            } else if (o1.getLevel() > o2.getLevel()) {
                return -1;
            }
            return 0;
        });

        for (int i = 0; i < ChunkMemoryAllotEnum.values().length; i++) {
            MEMORY_LEVEL.add(ChunkMemoryAllotEnum.values()[i]);
        }

        int level = 3;

        int index = 0;

        // for (ChunkMemoryAllotEnum chunkMemoryAllotEnum : MEMORY_LEVEL) {
        // if ((chunkMemoryAllotEnum.getLevel() & level) ==
        // chunkMemoryAllotEnum.getLevel()) {
        // System.out.println("第一次的级别:" + chunkMemoryAllotEnum.getLevel());
        // index = chunkMemoryAllotEnum.getMoveBit();
        // break;
        // }
        //
        // }

        for (ChunkMemoryAllotEnum chunkMemoryAllotEnum : MEMORY_LEVEL) {
            if (index == 0) {
                if ((chunkMemoryAllotEnum.getLevel() & level) == chunkMemoryAllotEnum.getLevel()) {
                    System.out.println("第2次的级别:" + chunkMemoryAllotEnum.getLevel());
                    index = chunkMemoryAllotEnum.getMoveBit();
                }
            } else {
                if (chunkMemoryAllotEnum.getMoveBit() < index
                        && (chunkMemoryAllotEnum.getLevel() & level) == chunkMemoryAllotEnum.getLevel()) {
                    System.out.println("第2次的级别:" + chunkMemoryAllotEnum.getLevel());
                    index = chunkMemoryAllotEnum.getMoveBit();
                }
            }

        }
    }

    /**
    * 进行内存分配操作
    * 方法描述
    * @param size 需要的内存大小,最好CHUNK_SIZE的倍数，以方便 内存的回收利用
    * @return
    * @创建日期 2016年12月19日
    */
    public MycatBufferBase allocMem(int allocFlag, int size) {

        MycatBufferBase result = null;

        // 首次为0匹配上最想要的内存
        int index = 0;

        ChunkMemoryAllotEnum allot = null;
        while ((allot = this.memoryMatchlevel(allocFlag, index)) != null) {
            // 进行内存分配
            result = allot.getChunkAllot().allocMem(size);
            if (result == null) {
                continue;
            } else {
                break;
            }
        }
        return result;
    }

    /**
    * 进行内存的归还操作
    * 方法描述
    * @param buffer
    * @创建日期 2016年12月19日
    */
    public void recyleMem(MycatBufferBase buffer) {

        // 明确内存的分配方式进行归还操作
    }

}
