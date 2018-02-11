package com.mydubbo.redis;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;

/**
 * REDIS
 */

public class RedisProUtil {

    private static Properties prop = new Properties();
    private static Logger log = Logger.getLogger(RedisProUtil.class);

    static {
        String configPath = null;
        try {
            configPath = "redis.properties";
            log.info("<<信息>> 加载redis基本配置信息(" + configPath + ")......");
            InputStream _file = RedisProUtil.class.getClassLoader().getResourceAsStream(configPath);
            prop.load(_file);
            log.info("<<信息>> 加载redis基本配置信息成功(" + configPath + ")......");
        } catch (Exception e) {
            log.error("<<异常信息>> 加载redis基本配置信息(" + configPath + ")出现异常:" + e);
        }
    }

    public static String getValueByKeyName(String name) {
        return (String) prop.getProperty(name);
    }
}
