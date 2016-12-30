package io.mycat.bigmem.sqlcache;

import io.mycat.bigmem.console.LocatePolicy;
import io.mycat.bigmem.sqlcache.impl.mmap.MappedBigCache;

import java.io.IOException;
import java.util.Iterator;

/**
 * SQL大结果集缓存
 *
 * @author zagnix
 * @version 1.0
 * @create 2016-12-27 18:48
 */

public class BigSQLResultCache implements Iterator {

    private IBigCache bigSqlCache;
    private String cacheDir;

    public BigSQLResultCache(LocatePolicy locatePolicy,String sqlkey,int pageSize){
        /**
         * Core - DirectMemory
         * Normal - mmap
         */
        //TODO
        this.cacheDir = "bigcache/"+sqlkey.hashCode();

        if (locatePolicy.equals(LocatePolicy.Normal)){
            try {
                bigSqlCache = new MappedBigCache(cacheDir,"sqlcache",pageSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(locatePolicy.equals(LocatePolicy.Core)){

        }
    }

    /**
     * 添加一条sql二进制数据到Cache存储中
     * @param data
     */
    public void put(byte [] data){
        try {
            bigSqlCache.put(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据是否为空
     * @return
     */
    public boolean isEmpty(){
        return bigSqlCache.isEmpty();
    }

    /**
     * 还有下一条sql结果集？
     * @return
     */
    public boolean hasNext() {
        return !bigSqlCache.isEmpty();
    }

    /**
     * 取下一条sql结果集
     *
     * @return
     */
    public byte[] next()  {
        try {
            return bigSqlCache.next();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Iterator interface
     */
    public void remove() {
        //TODO
    }

    /**
     * 主动将内存映射文件的数据刷到磁盘中
     */
    public void flush(){
        bigSqlCache.flush();
    }

    /**
     * sql 结果集大小
     * @return
     */
    public long size(){
        return bigSqlCache.size();
    }

    /**
     * 从 PageLRUCache中移除Page，并执行unmap操作，并删除对于文件
     */
    public void removeAll(){
        try {
            bigSqlCache.removeAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 从 PageLRUCache中移除Page，并执行unmap操作，但不删除文件
     * 下次运行时候可以读取文件内容
     */
    public void recycle(){
        try {
            bigSqlCache.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 复位读位置为0，从头开始读取sql结果集
     */
    public void reset(){
        bigSqlCache.reset();
    }
}
