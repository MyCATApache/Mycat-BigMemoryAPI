package io.mycat.bigmem.buffer;

import io.mycat.bigmem.util.MpsQueue;

public class CacheThreadLocal<T> {
	
	protected Arena arena;
	protected final int tinySize ; /*tiny 类型缓存多少个*/
	protected final int smallSize ; /*small 类型缓存多少个*/ 
	protected final int normalSize ; /*normal 类型缓存多少个*/
	protected final boolean cached; /*是否进行缓存*/
	protected int pageSize;
	protected int maxOrder;
	private MpsQueue<T>[] tinyCache =null;
	private MpsQueue<T>[] smallCache = null;
	private MpsQueue<T>[] normalCache = null;
	
	public CacheThreadLocal(Arena arena, int pageSize,int maxOrder, int tinySize, int smallSize,int normalSize) {
		this.arena = arena;
		this.pageSize = pageSize;
		this.tinySize = tinySize;
		this.smallSize = smallSize;
		cached = true;
		
		this.normalSize = normalSize;
		if(this.tinySize > 0) {
			initCache(tinyCache, arena.getTinySubpagePoolSize(), tinySize);
		}
		
		if(this.smallSize > 0) {
			initCache(smallCache, arena.getSmallSubpagePoolSize(), smallSize);
		}
		
		if(this.normalSize > 0) {
			
		}
	}
	@SuppressWarnings("unchecked")
	private void initCache(MpsQueue<T>[] cache,int size, int cacheSize) {
		cache = new MpsQueue[size];
		for(int i = 0 ; i < size; i++) {
			cache[i] = new MpsQueue<>(cacheSize);
		}
		
	}
//	public CacheThreadLocal() {
//		cached = false;
//	}
	
	
}
