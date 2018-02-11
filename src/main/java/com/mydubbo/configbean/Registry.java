package com.mydubbo.configbean;

import com.mydubbo.redis.RedisPoolerUtil;
import org.springframework.beans.factory.InitializingBean;

import java.io.Serializable;

/**
 * Created by xiangsong on 2018/2/3.
 */
public class Registry extends BaseConfigBean implements InitializingBean {

    private String protocol;
    private String ip;
    private int port;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Registry{" +
                "protocol='" + protocol + '\'' +
                ", ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                '}';
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (protocol.equalsIgnoreCase("redis")) {
            //初始化redis
            boolean flag = RedisPoolerUtil.initJdeisPool(ip, port, null);
            if (!flag) {
                throw new RuntimeException("<<注册中心>> redis服务信息初始化失败! redis信息：" + this);
            }
        }
    }
}
