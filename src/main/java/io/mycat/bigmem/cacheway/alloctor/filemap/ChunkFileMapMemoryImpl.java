package io.mycat.bigmem.cacheway.alloctor.filemap;

import java.io.IOException;

import io.mycat.bigmem.buffer.DirectMemAddressInf;
import io.mycat.bigmem.buffer.MycatBufferBase;
import io.mycat.bigmem.buffer.impl.MapFileBufferImp;
import io.mycat.bigmem.cacheway.alloctor.ChunkMemoryAllotInf;

/**
 * 进行直接可移动的大内存的分配操作
 * @author liujun
 * 2016年12月29日
 */
public class ChunkFileMapMemoryImpl implements ChunkMemoryAllotInf {

    /**
     * 内存池对象信息
     * @字段说明 pool
     */
    private FileMapBufferPage[] POOL;

    /**
    * 每个chunk的大小
    * @字段说明 CHUNK_SIZE
    */
    private int CHUNK_SIZE;

    /**
     * 初始化标识
     */
    private boolean initFlag;

    @Override
    public void allotorInit(int memSize, int chunkSize, short poolSize) {
        CHUNK_SIZE = chunkSize;
        // 进行每个内存页的初始化
        POOL = new FileMapBufferPage[poolSize];
        try {
            // 进行每个chunk的页面的分配内存操作,每个mapfile文件默认为128M
            for (int i = 0; i < poolSize; i++) {
                POOL[i] = new FileMapBufferPage(new MapFileBufferImp(memSize), CHUNK_SIZE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("ChunkFileMapMemoryImpl allotorInit exception", e);
        }
        // 标识初始化完成
        initFlag = true;
    }

    @Override
    public MycatBufferBase allocMem(int size) {

        if (!initFlag) {
            throw new RuntimeException("ChunkDirectMemoryImpl memory not init ,please invoke allotorInit");
        }

        // 计算需要的chunk大小
        int needChunk = size % CHUNK_SIZE == 0 ? size / CHUNK_SIZE : size / CHUNK_SIZE + 1;
        // 取得内存页信息
        FileMapBufferPage page = null;
        for (FileMapBufferPage pageMemory : POOL) {
            if (pageMemory.checkNeedChunk(needChunk)) {
                page = pageMemory;
                break;
            }
        }

        // 如果能找合适的内存空间，则进行分配
        if (null != page) {
            // 针对当前的chunk进行内存的分配操作
            MycatBufferBase buffer = page.alloactionMemory(needChunk);
            return buffer;
        }
        return null;
    }

    @Override
    public void recyleMem(MycatBufferBase buffer) {

        if (buffer.limit() < buffer.capacity()) {

            // 计算chunk归还的数量
            int chunkNum = (int) (buffer.capacity() - buffer.limit()) / CHUNK_SIZE;

            // 获得内存buffer
            DirectMemAddressInf thisNavBuf = (DirectMemAddressInf) buffer;
            // attachment对象在buf.slice();的时候将attachment对象设置为总的buff对象
            DirectMemAddressInf parentBuf = (DirectMemAddressInf) thisNavBuf.getAttach();

            int chunkAdd = buffer.limit() % CHUNK_SIZE == 0 ? (int) buffer.limit() / CHUNK_SIZE
                    : (int) buffer.limit() / CHUNK_SIZE + 1;
            // 已经使用的地址减去父类最开始的地址，即为所有已经使用的地址，除以chunkSize得到chunk当前开始的地址,得到整块内存开始的地址
            int startChunk = (int) ((thisNavBuf.address() - parentBuf.address()) / CHUNK_SIZE) + chunkAdd;

            boolean recyProc = false;

            for (FileMapBufferPage pageMemory : POOL) {
                if ((recyProc = pageMemory.recycleBuffer((MycatBufferBase) parentBuf, startChunk, chunkNum)) == true) {
                    break;
                }
            }

            if (!recyProc) {
                System.out.println("memory recycle fail");
            }
        } else {
            System.out.println("not memory recycle");
        }

    }

}
