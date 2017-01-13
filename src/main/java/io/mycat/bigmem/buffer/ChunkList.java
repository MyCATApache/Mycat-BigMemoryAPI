package io.mycat.bigmem.buffer;

import io.mycat.bigmem.util.StringUtil;

/**
*@desc
*@author: zhangwy   @date: 2016年12月28日 上午6:50:37
**/
public class ChunkList<T> {
	private final Arena<T> arena;
	private  ChunkList<T> preList;
	private final ChunkList<T> nextList;
	
	private final int minUsage;
	private final int maxUsage;
	private  Chunk<T> head;
	
	/**
	 * 初始化Chunk使用率的链表
	 */
	public ChunkList(Arena<T> arena, ChunkList<T> nextList, int minUsage, int maxUsage) {
		this.arena = arena;
		this.nextList = nextList;
		this.minUsage = minUsage;
		this.maxUsage = maxUsage;
	}
	public void setPre(ChunkList<T> preList) {
		this.preList = preList;
	}
	/**
	*@desc 返回当前chunk是否仍然在使用
	*@auth zhangwy @date 2017年1月4日 下午10:52:01
	**/
	public boolean free(Chunk<T> chunk, long handle) {
		chunk.free(handle);
		if(chunk.usage() < minUsage) {
			remove(chunk);
			return move0(chunk);
		}
		return true;
	}
	/**
	*@desc 判断之前是否还有prelist，即还未到到达q000,比q000还小即使用率为0,可以将当前chunk进行释放
	*@auth zhangwy @date 2017年1月4日 下午11:32:04
	**/
	private boolean move0(Chunk<T> chunk) {
		if(preList == null) {
			//不插入了,直接返回
			return false;
		}
		return preList.move(chunk);
	}
	/**
	*@desc 判断当前的chunk使用率是否小于 chunkList的最小使用率,
	*如果小于,
	*		 判断前一个使用率是否存在,存在比较是否插入,不存在移除当前chunk
	*,如果大于,
	*		进行插入.
	*@auth zhangwy @date 2017年1月4日 下午11:35:02
	**/
	private boolean move(Chunk<T> chunk) {
		if(chunk.usage() < minUsage) {
			return move0(chunk);
		}
		add0(chunk);
		return true;
	}
	/**
	 * 分配一个normalSize的byteBuffer
	 * **/
	public boolean allocate(BaseByteBuffer<T> byteBuffer ,int capacity, int normalSize) {
		if(head == null) return false;
		Chunk<T> cur = head;
		while(cur != null) {
			long handle = cur.allocate(normalSize);
			if(handle > 0) {
				cur.initBuf(byteBuffer, handle, capacity);
				if(cur.usage() >= maxUsage) {
					remove(cur);
					addChunk(cur);
				}
				return true;
			}
			cur = cur.next;
		}
		return false;
	}
	
	
	/**
	 * 分配一个normalSize的 的handle
	 * **/
	public Handle allocateHandle(int capacity, int normalSize) {
		if(head == null) return null;
		Chunk<T> cur = head;
		while(cur != null) {
			long handle = cur.allocate(normalSize);
			if(handle > 0) {
				Handle handleObj = new Handle(handle, cur, normalSize);
				if(cur.usage() >= maxUsage) {
					remove(cur);
					addChunk(cur);
				}
				return handleObj;
			}
			cur = cur.next;
		}
		return null;
	}
	
	/**
	*@desc 链表添加一个chunk,主要跟maxUsage进行比较 
	*@return void
	*@auth zhangwy @date 2016年12月30日 上午7:39:20
	**/
	public void addChunk(Chunk<T> cur) {
		if(cur.usage() >= maxUsage) {
			nextList.addChunk(cur);
			return ;
		}
		add0(cur);
	}
	/**
	*@desc 将chunk加入当前使用率的链表中。
	*@auth zhangwy @date 2017年1月4日 下午11:29:11
	**/
	private void add0(Chunk<T> cur) {
		cur.parent = this;
		if(head == null) {
			head = cur;
			cur.next = null;
			cur.prev = null;
		} else {
			cur.next = head;
			head.prev = cur;
			cur.prev = null;
			head = cur;
		}
	}
	/**
	*@desc: 将chunk从链表中移除
	*@return: void
	*@auth: zhangwy @date: 2016年12月30日 上午7:27:48
	**/
	private void remove(Chunk<T> cur) {
		if(head == cur) {
			head = cur.next;
			if(head != null) {
				//将头指针.prev 向null
				head.prev = null;
			}
		} else {
			Chunk<T> next = cur.next;
			cur.prev.next = next;
			if(next != null) {
				next.prev = cur.prev;
			}
		}
	}
	

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		Chunk<T> next = head;
		while(next != null) {
			sb.append(next);
			sb.append(StringUtil.NEWLINE);
			next = next.next;
		}
		
		return sb.toString();
	}
}

