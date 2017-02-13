package io.mycat.bigmem.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 进行资源文件的公共查找
 * 
 * or kk
 *  2016年12月31日
 */
public class PropertiesUtils {

    private Logger log = Logger.getLogger(PropertiesUtils.class);

    private static final Map<Object, Object> PROPER_MAP = new HashMap<Object, Object>();

    private static final PropertiesUtils PROINSTANCE = new PropertiesUtils();

    static {
        // 加载配制文件信息
        PROINSTANCE.loadProperties("cfg.properties");
    }

    private PropertiesUtils() {

    }

    public static PropertiesUtils getInstance() {
        return PROINSTANCE;
    }

    private void loadProperties(String fileName) {
        // 进行加载数据
        Properties pro = new Properties();

        try {
            pro.load(PropertiesUtils.class.getResourceAsStream("/" + fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("PropertiesUtils load properties not exception:", e);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("PropertiesUtils load properties IOException:", e);
        }

        if (null != pro.entrySet()) {
            Iterator<Entry<Object, Object>> entryIter = pro.entrySet().iterator();

            while (entryIter.hasNext()) {
                Entry<Object, Object> entry = entryIter.next();
                PROPER_MAP.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public String getValue(Object key) {
        Object rsp = PROPER_MAP.get(key);

        if (null != rsp) {
            return String.valueOf(rsp);
        }

        return null;
    }

    public static void main(String[] args) {

        System.out.println(PropertiesUtils.getInstance().getValue("IBS.Schedule.is.test.flag"));

    }
}
