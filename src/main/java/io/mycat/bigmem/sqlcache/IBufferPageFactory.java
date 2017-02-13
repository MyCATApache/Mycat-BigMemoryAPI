package io.mycat.bigmem.sqlcache;

        import java.io.IOException;
        import java.util.Set;

/**
 *  Buffer  Page Factory 接口
 *
 * or zagnix
 *  2016-12-27 22:59
 */
public interface IBufferPageFactory {

    /**
     * 根据index得到一页
     *
     * @param index 参数
     * @return 参数
     * @throws IOException 参数
     */
    public MyCatBufferPage acquirePage(long index) throws IOException;

    /**
     * 回收Page
     *
     * @param index 参数
     */
    public void releasePage(long index);

    /**
     * 删除一页
     * @param index 参数
     * @throws IOException 参数
     */
    public void deletePage(long index) throws IOException;


    /**
     * 刷磁盘
     */
    public void flush();

    /**
     * 删除所有页
     * @throws IOException 参数
     */
    public void deleteAllPages() throws IOException;


    /**
     * 得到所有的页
     * @return 参数
     */
    public Set<Long> getExistingPageIndexSet();


    /**
     * 从cache中移除所有的页
     * @throws IOException 参数
     */
    public void releaseCachedPages() throws IOException;

}
