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
                bigSqlCache = new MappedBigCache(cacheDir,"sqlcache",32*1024*1024);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(locatePolicy.equals(LocatePolicy.Core)){

        }
    }

    /**
     *
     * @param data
     */
    public void put(byte [] data){
        try {
            bigSqlCache.put(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isEmpty(){
        return bigSqlCache.isEmpty();
    }
    public boolean hasNext() {
        return !bigSqlCache.isEmpty();
    }

    public byte[] next()  {
        try {
            return bigSqlCache.next();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void remove() {

    }

    public void flush(){
        bigSqlCache.flush();
    }
    public long size(){
        return bigSqlCache.size();
    }
    public void removeAll(){
        try {
            bigSqlCache.removeAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recycle(){
        try {
            bigSqlCache.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
