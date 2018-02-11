package com.mydubbo.redis;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.List;
import java.util.Map;

/**
 * Redis 连接池实例
 */

public class RedisPoolerUtil {

    private static Logger log = Logger.getLogger(RedisPoolerUtil.class);

    private static JedisPool pool;

    /**
     * 初始化redis池
     */
    public  static boolean initJdeisPool(String ip, int port, String password) {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(Integer.parseInt(RedisProUtil.getValueByKeyName("REDIS.MaxActive")));
            config.setMaxIdle(Integer.parseInt(RedisProUtil.getValueByKeyName("REDIS.MaxIdle")));
            config.setMinIdle(Integer.parseInt(RedisProUtil.getValueByKeyName("REDIS.MinIdle")));
            config.setMaxWaitMillis(Integer.parseInt(RedisProUtil.getValueByKeyName("REDIS.MaxWaitMillis")));
            //空闲配置
            config.setTestWhileIdle(Boolean.parseBoolean(RedisProUtil.getValueByKeyName("REDIS.TestWhileIdle")));
            config.setMinEvictableIdleTimeMillis(Integer.parseInt(RedisProUtil.getValueByKeyName("REDIS.MinEvictableIdleTimeMillis")));
            config.setTimeBetweenEvictionRunsMillis(Integer.parseInt(RedisProUtil.getValueByKeyName("REDIS.TimeBetweenEvictionRunsMillis")));
            config.setNumTestsPerEvictionRun(Integer.parseInt(RedisProUtil.getValueByKeyName("REDIS.NumTestsPerEvictionRun")));
            config.setTestOnBorrow(Boolean.parseBoolean(RedisProUtil.getValueByKeyName("TestOnBorrow")));
            config.setTestOnReturn(Boolean.parseBoolean(RedisProUtil.getValueByKeyName("TestOnReturn")));
            int connectionTimeOut = Integer.parseInt(RedisProUtil.getValueByKeyName("REDIS.ConnectionTimeOut"));
            log.info("<<注册中心>> redis服务信息:  当前连接的服务IP为：" + ip + ":" + port);

            if (StringUtils.isEmpty(password)) {
                pool = new JedisPool(config, ip, port, connectionTimeOut);
            } else {
                pool = new JedisPool(config, ip, port, connectionTimeOut, password);
            }

            log.info("<<注册中心>> redis服务信息:当前连接的服务IP为：" + ip + ":" + port + ",初始化成功!");
            return true;
        } catch (Exception e) {
            log.error("<<注册中心>> redis服务信息初始化失败,信息：" + e.getLocalizedMessage());
            return false;
        }
    }


    public static void publish(String channel, String msg) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.publish(channel, msg);
        } catch (Exception e) {

        } finally {
            returnResource(jedis);
        }
    }

    public static void subsribe(String channel, JedisPubSub ps) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.subscribe(ps, channel);
        } catch (Exception e) {

        } finally {
            returnResource(jedis);
        }
    }

    public static Long hdel(String key, String key1) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hdel(key, key1);
        } catch (Exception e) {

        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public static String get(String key) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = pool.getResource();
            value = jedis.get(key);
        } catch (Exception e) {

        } finally {
            returnResource(jedis);
        }
        return value;
    }

    public static boolean exists(String key) {
        Jedis jedis = null;
        boolean value = false;
        try {
            jedis = pool.getResource();
            value = jedis.exists(key);
        } catch (Exception e) {

        } finally {
            returnResource(jedis);
        }
        return value;
    }

    public static String set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.set(key, value);
        } catch (Exception e) {

            return "0";
        } finally {
            returnResource(jedis);
        }
    }

    public static String set(String key, String value, int expire) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.expire(key, expire);
            return jedis.set(key, value);
        } catch (Exception e) {

            return "0";
        } finally {
            returnResource(jedis);
        }
    }

    public static Long del(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.del(key);
        } catch (Exception e) {
            return null;
        } finally {
            returnResource(jedis);
        }
    }


    public static Long lpush(String key, String... strings) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, strings);
        } catch (Exception e) {

            return 0L;
        } finally {
            returnResource(jedis);
        }
    }

    public static List<String> lrange(String key) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key, 0, -1);
        } catch (Exception e) {
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    public static String hmset(String key, Map map) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hmset(key, map);
        } catch (Exception e) {
            return "0";
        } finally {
            returnResource(jedis);
        }
    }

    public static List<String> hmget(String key, String... strings) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = pool.getResource();
            return jedis.hmget(key, strings);
        } catch (Exception e) {

        } finally {
            returnResource(jedis);
        }
        return null;
    }

    private static void returnResource(Jedis redis) {
        if (redis != null) {
            redis.close();
        }
    }

}
