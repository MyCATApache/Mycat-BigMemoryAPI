package io.mycat.bigmem.mempool;

/**
 * 伙伴算法实现，最小分配单元是页，
 * 可以连续分配多个页，回收时候可以合并多个页
 * Created by znix on 2017/1/4.
 */
public class PoolBuddyChunk<T> {

    private T memory;
    private final int chunkSize;
    private final int maxOrder;
    private final int pageSize;
    private final int pageShifts;
    private final int log2ChunkSize;

    private final int maxSubpageAllocs;
    private final byte[] memoryMap;
    private final byte[] depthMap;

    /** Used to mark memory as unusable */
    private final byte unusable;

    private int freeBytes;

    private final int subpageOverflowMask;


    public PoolBuddyChunk(final int chunkSize,final int maxOrder,final int pageSize){
        this.chunkSize = chunkSize;
        this.maxOrder = maxOrder;
        this.pageSize = pageSize;
        this.pageShifts = log2(pageSize);
        this.log2ChunkSize = log2(chunkSize);
        this.maxSubpageAllocs = 1 << maxOrder;
        this.unusable = (byte) (maxOrder+1);
        this.memoryMap = new byte[maxSubpageAllocs];
        this.depthMap = new byte[maxSubpageAllocs];
        this.freeBytes  = chunkSize;
        this.subpageOverflowMask = ~(pageSize-1);

        int memoryMapIndex = 1;
        for (int d = 0; d <=maxOrder ; ++d) {
            int depth = 1 << d;
            for (int i = 0; i < depth; i++) {
                memoryMap[memoryMapIndex] = (byte)d;
                depthMap[memoryMapIndex] = (byte)d;
                memoryMapIndex++;
            }
        }
    }


    long allocate(int remCapacity){

        if ((remCapacity&subpageOverflowMask)!=0){
            return allocateRun(remCapacity);
        }

        return -1L;
    }

    /**
     * 分配内存remCapacity大小，remCapacity满足pageSize的倍数
     *
     * @param remCapacity
     * @return
     */
    long allocateRun(int remCapacity){
        /**
         * 平衡二叉树深度为d满足请求remCapacity大小内存，
         */
        int d = maxOrder - (log2(remCapacity) - pageShifts);

        int id = allocateNode(d);

        if(id < 0){
            return -1;
        }
        freeBytes -= runLength(id);
        return id;
    }

    /**
     * 通过 handle free分配的内存
     * @param handle
     */
    private void free(long handle){
        int memoryMapIdx = (int) handle;
        freeBytes += runLength(memoryMapIdx);
        setValue(memoryMapIdx, depth(memoryMapIdx));
        updateParentsFree(memoryMapIdx);
    }

    /**
     * 递归更新其父节点id对应memoryMap数组下标的值
     * @param id
     */
    private void updateParentsFree(int id) {
        int logChild = depth(id) + 1;
        while (id > 1) {
            int parentId = id >>> 1;
            byte val1 = value(id);
            byte val2 = value(id ^ 1);
            logChild -= 1; // in first iteration equals log, subsequently reduce 1 from logChild as we traverse up

            if (val1 == logChild && val2 == logChild) {
                setValue(parentId, (byte) (logChild - 1));
            } else {
                byte val = val1 < val2 ? val1 : val2;
                setValue(parentId, val);
            }

            id = parentId;
        }
    }

    /**
     * 计算下标id的根节点下，内存大小
     * 可能包含连续多个页。
     * @param id
     * @return
     */
    private int runLength(int id) {
        return 1 <<(log2ChunkSize - depth(id));
    }


    /**
     * 根据id，返回depthMap数组对应的值
     * @param id
     * @return
     */
    private byte depth(int id) {
        return depthMap[id];
    }

    /**
     * 计算val的log2的值
     * @param val
     * @return
     */
    private static int log2(int val) {
        return Integer.SIZE - 1 - Integer.numberOfLeadingZeros(val);
    }

    /**
     * 返回数组memoryMap下标id，对应的值
     * @param id
     * @return
     */
    private byte value(int id) {
        return memoryMap[id];
    }

    /**
     * 从平衡二叉树深度d开始，找到满足要求的树节点(子节点或者叶子节点)
     * @param d
     * @return
     */
    private int allocateNode(int d){
        int id = 1;
        byte val = value(id);

        int initval= -(1 << d);

        /**
         * 说明chunk已经分配完成，无法继续分配了。
         */
        if (val > d){
            return -1;
        }


        while (val < d || (id & initval) ==0){
            id <<= 1;
            val = value(id);

            if (val > d){
                /**
                 * 右子节点
                 */
                id ^=1;
                val = value(id);
            }
        }
        byte value = value(id);
        setValue(id,unusable);
        updateParentsAlloc(id);
        return id;
    }


    /**
     * 节点id，递归更新父节点memoryMap的值
     * @param id
     */
    private void updateParentsAlloc(int id) {
        while (id > 1) {
            int parentId = id >>> 1;
            byte val1 = value(id);
            byte val2 = value(id ^ 1);
            byte val = val1 < val2 ? val1 : val2;
            setValue(parentId, val);
            id = parentId;
        }
    }

    /**
     * 设置memoryMap数组下标id的对应值为val
     *
     * @param id
     * @param val
     */
    private void setValue(int id, byte val) {
        memoryMap[id] = val;
    }
}
