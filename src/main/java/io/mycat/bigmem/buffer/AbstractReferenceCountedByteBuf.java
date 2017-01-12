package io.mycat.bigmem.buffer;

import io.mycat.bigmem.util.IllegalReferenceCountException;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import static io.mycat.bigmem.util.internal.ObjectUtil.checkPositive;

/**
 * Created by tracywwp on 2017/1/12 0012.
 *
 * 抽象基类{@link BaseByteBuffer }实现计数参考。
 */
public abstract class AbstractReferenceCountedByteBuf extends BaseByteBuffer {
    private static final AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuf> refCntUpdater =
            AtomicIntegerFieldUpdater.newUpdater(AbstractReferenceCountedByteBuf.class, "refCnt");

    private volatile int refCnt = 1;

    protected AbstractReferenceCountedByteBuf(int maxCapacity) {
        super(maxCapacity);
    }

    @Override
    public int refCnt() {
        return refCnt;
    }

    /**
     * 由子类直接使用的不安全操作，该类直接设置缓冲区的引用计数
     */
    protected final void setRefCnt(int refCnt) {
        this.refCnt = refCnt;
    }

    @Override
    public BaseByteBuffer retain() {
        return retain0(1);
    }

    @Override
    public BaseByteBuffer retain(int increment) {
        return retain0(checkPositive(increment, "increment"));
    }

    private BaseByteBuffer retain0(int increment) {
        for (;;) {
            int refCnt = this.refCnt;
            final int nextCnt = refCnt + increment;

            //确保不会重复使用（意味着refcnt为0），同时我们遇到一个溢出
            if (nextCnt <= increment) {
                throw new IllegalReferenceCountException(refCnt, increment);
            }
            if (refCntUpdater.compareAndSet(this, refCnt, nextCnt)) {
                break;
            }
        }
        return this;
    }

    @Override
    public BaseByteBuffer touch() {
        return this;
    }

    @Override
    public BaseByteBuffer touch(Object hint) {
        return this;
    }

    @Override
    public boolean release() {
        return release0(1);
    }

    @Override
    public boolean release(int decrement) {
        return release0(checkPositive(decrement, "decrement"));
    }

    private boolean release0(int decrement) {
        for (;;) {
            int refCnt = this.refCnt;
            if (refCnt < decrement) {
                throw new IllegalReferenceCountException(refCnt, -decrement);
            }

            if (refCntUpdater.compareAndSet(this, refCnt, refCnt - decrement)) {
                if (refCnt == decrement) {
                    deallocate();
                    return true;
                }
                return false;
            }
        }
    }
    /**
     *
     * refCnt()方法是否等于0
     */
    protected abstract void deallocate();
}
