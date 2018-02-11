package com.mydubbo.configbean;

import com.mydubbo.rpc.rmi.server.RmiUtil;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by xiangsong on 2018/2/3.
 */
public class Protocol extends BaseConfigBean implements InitializingBean {

    private String name;
    private String ip;
    private String port;
    private String contextpath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getContextpath() {
        return contextpath;
    }

    public void setContextpath(String contextpath) {
        this.contextpath = contextpath;
    }

    @Override
    public String toString() {
        return "Protocol{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", contextpath='" + contextpath + '\'' +
                '}';
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (name.equalsIgnoreCase("rmi")) {
            RmiUtil rmi = new RmiUtil();
            rmi.startRmiServer(ip, port, "mydubboRMI");
        }
    }
}
