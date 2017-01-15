package io.mycat.bigmem.util;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;

/*cas的缓存循环数组,
 * 存数据:采用先占用位置,然后在放数据的原则.
 * 取数据:采用先取数据,置为cas置为空,然后在将读指针加一的原则,
 * 		需要注意的时候,由于是循环数组,取出来数据不为空,有可能已经是又被放进去的数据了.要保证当前reader指针没被改变的情况下才可以去cas改变置为空.
 * @auth zhangwy 
 * */
public class MpsQueue<T> {
	protected AtomicLong readerIndex;
	protected AtomicLong writerIndex;
	protected final int size ;
	protected final int mask ;
	AtomicReferenceArray<T> buffer = null;
	public MpsQueue(int length) {
		size = MathUtil.roundToPowerOfTwo(length) ;
		mask = size - 1;
		buffer = new AtomicReferenceArray<T>(size);
		readerIndex = new AtomicLong(0);
		writerIndex = new AtomicLong(0);
	}
	//idx % size
	public int idx(long index) {
		return (int) (index & mask);
	}
	//读指针获取
	public long getReaderIndex() {
		return readerIndex.get();
	}
	//写指针获取
	public long getWriterIndex() {
		return writerIndex.get();
	}
	/*放置元素*/
	public boolean put(T element) {
		long reader ,writer ;
		long wrapSize ;
		if(element == null)	{
			throw new IllegalArgumentException("element can't not be null");
		}
		do {
			reader = getReaderIndex();
			writer = getWriterIndex();
			wrapSize = writer - reader ;
			//仍有空间放置
			if(wrapSize < size) {
			} else {
				return false;
			}
		} while(!writerIndex.compareAndSet(writer, writer + 1));
		int offset = idx(writer);
		if(buffer.get(offset) != null) {
			throw new RuntimeException("buffer @" + writer + " must be null,but now is not null");
		}
		buffer.set(offset, element);
		return true;
	}
	/**
	 ** get a element from the queue 
	 ***/
	public T get() {
		T element  = null;
		long reader ,writer ;
		int offset;
		do {
			reader = getReaderIndex() ;
			writer =  getWriterIndex();
			/*queue不为空*/
			if(writer == reader) {
				return null;
			}
			offset = idx(reader);
			element = buffer.get(offset);
			/*元素已经被取走了*/
			if(element == null ) continue;
			/*reader已经是最新*/
			if(reader == getReaderIndex() && buffer.compareAndSet(offset, element, null)) {
				//System.out.println("pop :" +reader +":"+ offset + ":" + element);
				break;
			}
		}while(true);
		if(element != null) {
			while(!readerIndex.compareAndSet(reader, reader + 1)){
				throw new RuntimeException(String.format("readerIndex is %d has been changed,is error", readerIndex));
			}
			return element;
		}
		return null;
	}
	/**
	 * 
	 */
	
}
