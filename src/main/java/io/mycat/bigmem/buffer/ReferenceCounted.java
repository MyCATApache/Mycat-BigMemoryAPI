package io.mycat.bigmem.buffer;

/**
 * Created by tracywwp on 2017/1/11 0011.
 * 一个引用计数的对象，需要显式释放。
 */
public interface ReferenceCounted {

    /**
     * 返回此对象的引用计数
     * 这意味着该对象已被释放。
     */
    int refCnt();

    /**
     * 增加参考计数 +1
     */
    ReferenceCounted retain();

    /**
     * 通过指定增加引用计数
     */
    ReferenceCounted retain(int increment);

    /**
     *
     * 为调试目的记录当前对象的访问位置.
     * 如果这个对象是确定被泄露，信息记录该操作将提供给您通过
     */
    ReferenceCounted touch();

    /**
     *
     * 记录该对象的当前访问的位置，用于调试目的额外的任意信息。
     */
    ReferenceCounted touch(Object hint);

    /**
     *减少引用计数的{ 1 } @代码和回收这个对象如果引用计数达到{ 0 }
     *
     */
    boolean release();

    /**
     * 减少指定数的引用计数
     *
     */
    boolean release(int decrement);
}
